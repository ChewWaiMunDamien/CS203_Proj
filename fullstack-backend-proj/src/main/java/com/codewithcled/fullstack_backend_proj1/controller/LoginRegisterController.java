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
import com.codewithcled.fullstack_backend_proj1.config.JwtProvider;

import java.util.Optional;
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
@RequestMapping("/login")
public class LoginRegisterController {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.token}")
    private String adminToken;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private UserServiceImplementation customUserDetails;

    @Autowired
    private UserService userService;

    @PostMapping("/validate-admin-token")
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

    // Create User to Database
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {

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


    //Login and authentication
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody User loginRequest) {
        AuthResponse authResponse = userService.signInUser(loginRequest);
        return new ResponseEntity<>(authResponse,HttpStatus.OK);
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

