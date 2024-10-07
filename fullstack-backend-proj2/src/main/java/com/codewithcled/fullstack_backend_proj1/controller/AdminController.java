package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.beans.factory.annotation.Value;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import com.codewithcled.fullstack_backend_proj1.service.TournamentService;
import com.codewithcled.fullstack_backend_proj1.service.UserService;
import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentMapper;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


/*ADMIN CONTROLLER
 * Authorisation: Can do whatever user can do plus
 * - Create/Delete tournaments
 * - Edit Tournament
 * - Create pseudo User for the database
 * - Edit players from the playerList
 * - Delete Players from the playerList
 * 
 */

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${admin.token}")
    private String adminToken;

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/signin/validate-admin-token")
    public ResponseEntity<?> validateAdminToken(@RequestBody TokenRequest tokenRequest) {
        if (adminToken.equals(tokenRequest.getToken())) {
            return ResponseEntity.ok(new TokenResponse(true));
        } else {
            return ResponseEntity.status(401).body(new TokenResponse(false));
        }
    }
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

    //Create Tournament
    @PostMapping("/tournament")
    public ResponseEntity<TournamentDTO> createTournament(@RequestBody CreateTournamentRequest tournament) {
        
        Tournament createdTournament;
        try {
            createdTournament = tournamentService.createTournament(tournament);
            TournamentDTO tournamentDTO = TournamentMapper.toDTO(createdTournament);
            return new ResponseEntity<>(tournamentDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // Return 400 Bad Request for errors
        }
    
    }

    // Create User to Database
    @PostMapping("/signup/user")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignUpRequest user) throws Exception {

        try {
            AuthResponse authResponse = userService.createUser(user);
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);  // Return 201 Created on success
        } catch (Exception ex) {
            System.out.println("EROR!");
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);  // Return 409 Conflict if username/email is taken
        }
//        catch (Exception ex) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 for other errors
//        }

    }

    //delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable("id") Long id){
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Return 404 if user not found
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();  // Return 204 No Content on successful deletion
    }


    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable("username") String email) {
        try {
            User newUser = userRepository.findByEmail(email);
            UserDTO userDto = UserMapper.toDTO(newUser);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
    }

    @DeleteMapping("/tournament/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable("id") Long id) {
        try {
            tournamentRepository.deleteById(id);
            return ResponseEntity.ok("Tournament with ID " + id + " has been deleted.");  // Return 200 OK with success message
        }  catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the tournament.");  // Return 500 Internal Server Error for other issues
        }
    }

    


    //editing players from the playerlist page
    @PutMapping("/user/{id}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody SignUpRequest newUser, @PathVariable("id") Long id) {
        return userService.updateUser(id, newUser)
                .map(updatedUser -> {
                    UserDTO userDTO = UserMapper.toDTO(updatedUser);
                    return ResponseEntity.ok(userDTO);  // Return 200 OK with the updated UserDTO
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());  // Return 404 if user not found
    }




   
}