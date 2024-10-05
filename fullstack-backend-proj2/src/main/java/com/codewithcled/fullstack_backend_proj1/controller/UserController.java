package com.codewithcled.fullstack_backend_proj1.controller;

import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import com.codewithcled.fullstack_backend_proj1.service.UserService;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentMapper;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import com.codewithcled.fullstack_backend_proj1.config.JwtProvider;

import java.util.Optional;

import javax.swing.text.html.HTML;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private UserServiceImplementation customUserDetails;

    @Autowired
    private UserService userService;

   
    public static class TokenResponse {
        private boolean valid;

        public TokenResponse(boolean valid) {
            this.valid = valid;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }

    public static class TokenRequest {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

//     // Create User to Database
//     @PostMapping("/signup/user")
//     public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {

//         try {
//             AuthResponse authResponse = userService.createUser(user);
//             return new ResponseEntity<>(authResponse, HttpStatus.CREATED);  // Return 201 Created on success
//         } catch (Exception ex) {
//             System.out.println("EROR!");
//             return new ResponseEntity<>(null, HttpStatus.CONFLICT);  // Return 409 Conflict if username/email is taken
//         }
// //        catch (Exception ex) {
// //            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 for other errors
// //        }

//     }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();  // Return 204 No Content if the list is empty
        }
        return ResponseEntity.ok(users);  // Return 200 OK with the list of users
    }

    //delete user
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable("id") Long id){
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Return 404 if user not found
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();  // Return 204 No Content on successful deletion
    }


    // //Login and authentication
    // @PostMapping("/signin")
    // public ResponseEntity<AuthResponse> signin(@RequestBody User loginRequest) {
    //     AuthResponse authResponse = userService.signInUser(loginRequest);
    //     return new ResponseEntity<>(authResponse,HttpStatus.OK);
    // }


    @GetMapping("/user/id/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserMapper.toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/email/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String email) {
        try {
            User newUser = userRepository.findByEmail(email);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
    }


    //editing players from the playerlist page
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User newUser, @PathVariable("id") Long id) {
        return userService.updateUser(id, newUser)
                .map(updatedUser -> ResponseEntity.ok(updatedUser))  // Return 200 OK with the updated user
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());  // Return 404 if user not found
    }


    @PutMapping("/{id}/participating_tournament/add")
    public ResponseEntity<User> updateUserParticipating(@RequestParam("tournament_id") Long tournament_id, @PathVariable("id") Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(tournament_id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));

        user.addCurrentTournament(currentTournament);
        User updatedUser =  userRepository.save(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);  // Return 200 OK with the updated user
}

    @PutMapping("/{id}/participating_tournament/remove")
    public ResponseEntity<User> removeUserParticipating(@RequestParam("tournament_id") Long tournamentId, @PathVariable("id") Long userId) {
        try {
            User updatedUser = userService.removeUserParticipatingTournament(userId, tournamentId);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);  // Return 200 OK with the updated user
        } catch (Exception e) {
            // Log the exception message for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Return 400 Bad Request for errors
        }
    }

    @GetMapping("/{id}/participating_tournament")
    public ResponseEntity<List<Tournament>> getUserParticipatingTournaments(@PathVariable("id") Long id) {
        try {
            List<Tournament> tournamentIds = userService.getUserParticipatingTournaments(id);
            return ResponseEntity.ok(tournamentIds);  // Return 200 OK with the list of tournament IDs
        } catch (Exception e) {
            // Log the exception message for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Return 400 Bad Request for errors
        }
    }

    @GetMapping("/{id}/currentTournament/current")
    public ResponseEntity<List<Tournament>> getUserCurrentParticipatingTournament(@PathVariable("id") Long id){
        try {
            List<Tournament> tournaments = userService.getUserParticipatingTournaments(id);
            return ResponseEntity.ok(tournaments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }



    private Authentication authenticate(String username, String password) {

        System.out.println(username+"---++----"+password);

        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        System.out.println("Sign in in user details"+ userDetails);


        if(userDetails == null) {
            System.out.println("Sign in details - null" + userDetails);

            throw new BadCredentialsException("Invalid username and password");
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())) {
            System.out.println("Sign in userDetails - password mismatch"+userDetails);

            throw new BadCredentialsException("Invalid password");

        }
        System.out.println("HI " + userDetails.getAuthorities());
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

    }
}
