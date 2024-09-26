package com.codewithcled.fullstack_backend_proj1.controller;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import com.codewithcled.fullstack_backend_proj1.service.UserService;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;
import com.codewithcled.fullstack_backend_proj1.config.JwtProvider;
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
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserServiceImplementation customUserDetails;

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {

        String username= user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        String role = user.getRole();
        System.out.println(role);

        User isEmailExist = userRepository.findByEmail(email);
        if (isEmailExist != null) {
            throw new Exception("Email Is Already Used With Another Account");

        }
        User createdUser = new User();
        createdUser.setUsername(username);
        createdUser.setEmail(email);
        createdUser.setRole(role);
        createdUser.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(createdUser);
        userRepository.save(savedUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(email,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);


        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success");
        authResponse.setStatus(true);
        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

    }

    @GetMapping("/users")
    List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @PostMapping("/user/create")
    User newUser (@RequestBody User newUser){
        return userRepository.save(newUser);
    }

    @DeleteMapping("/user/{id}")
    String deleteTournament(@PathVariable("id") Long id){
//        if (!tournamentRepository.existsById(id)){
//
//        }
        userRepository.deleteById(id);
        return "user with " +id+ " has been deleted";
    }


    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam("username") String username) {
        boolean exists = userRepository.existsByUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {
        boolean exists = userRepository.existsByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/tournament")
    public ResponseEntity<String> getTournamentData() {
        // Example data; replace with actual logic
        return ResponseEntity.ok("Tournament Data");
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody User loginRequest) {
        System.out.println( loginRequest.getUsername());
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        //String role = loginRequest.getRole();

        System.out.println(username+"-------"+password);


        Authentication authentication = authenticate(username,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setMessage("Login success");

        User user =userRepository.findByEmail(username);
        authResponse.setRole(user.getRole());
        authResponse.setJwt(token);
        authResponse.setStatus(true);

        return new ResponseEntity<>(authResponse,HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    User getUserById(@PathVariable("id") Long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception("Error Occured"));
    }

    @PutMapping("/user/{id}")
    User updateTournament(@RequestBody User newUser, @PathVariable("id") Long id) throws Exception {
        return userRepository.findById(id)
                .map(user->{
                    user.setUsername(newUser.getUsername());
                    user.setElo(newUser.getElo());
                    user.setTournamentsParticipating(newUser.getTournamentsParticipating());

                    return userRepository.save(user);
                }).orElseThrow(() -> new Exception("Error Occured"));
    }

    @PutMapping("/user/{id}/participating_tournament/add")
    User updateUserParticipating(@RequestParam("tournament_id") Long tournament_id, @PathVariable("id") Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(tournament_id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));
    System.out.println(currentTournament);
        user.addParticipatingTournament(tournament_id);
        return userRepository.save(user);
    }

    @PutMapping("/user/{id}/participating_tournament/remove")
    User removeUserParticipating (@RequestParam("tournament_id") Long tournament_id, @PathVariable("id") Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(tournament_id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));
        System.out.println(currentTournament);
        user.removeParticipatingTournament(currentTournament);
        return userRepository.save(user);
    }

    @GetMapping("/user/{id}/participating_tournament")
    List<Long> getTournamentParticipants(@PathVariable("id") Long id) throws Exception {
        User currentUser =  userRepository.findById(id)
                .orElseThrow(() -> new Exception("Error Occured"));

        return currentUser.getTournamentsParticipating();
    }
    

//    @PutMapping("/user/{id}/participating_tournament/add")
//    User updateTournamentParticipant(@RequestBody Tournament newTournament, @PathVariable("id") Long id) throws Exception {
//        User currentUser =  userRepository.findById(id)
//                .orElseThrow(() -> new Exception("Error Occured"));
//        return userRepository.findById(id)
//                .map(tournament->{
//                    currentUser.addParticipatingTournament(newTournament);
//                    return userRepository.save(currentUser);
//                }).orElseThrow(() -> new Exception("Error Occured"));
//    }

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
