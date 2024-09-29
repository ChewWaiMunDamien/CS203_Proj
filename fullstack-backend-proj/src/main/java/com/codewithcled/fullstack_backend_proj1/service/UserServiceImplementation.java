package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.config.JwtProvider;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImplementation implements UserService,UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TournamentRepository tournamentRepository;

    private LocalDateTime lastChangeTimestamp = LocalDateTime.now();

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository=userRepository;
    }

    @Override
    public List<UserDTO> getUserChanges() {
       List<User> changes = userRepository.findChangesSince(lastChangeTimestamp);
        lastChangeTimestamp = LocalDateTime.now(); // Update the timestamp
        return changes.stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(),user.getEmail(),  user.getRole(),user.getElo()))
                .collect(Collectors.toList());
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        System.out.println(user);

        if(user==null) {
            throw new UsernameNotFoundException("User not found with this email"+username);

        }

        System.out.println("Loaded user: " + user.getEmail() + ", Role: " + user.getRole());
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User findUserProfileByJwt(String jwt) {
        // Implement logic to find user by JWT or remove if not needed
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserById(String userId) {
        // Implement logic to find user by ID or remove if not needed
        return null;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
     @Override
    public List<UserDTO> findAllUsersDTO() {
        return userRepository.findAll().stream()
        .map(user -> new UserDTO(user.getId(),user.getUsername(),user.getEmail(),user.getRole(),user.getElo()))
        .collect(Collectors.toList());
    }

    @Override
    public User loadByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public AuthResponse createUser(User user) throws Exception {
        String username= user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        String role = user.getRole();
        Integer elo = user.getElo();

        User isEmailExist = userRepository.findByEmail(email);
        if (isEmailExist != null) {
            System.out.println("Email Taken!");
            throw new Exception("Email Is Already Used With Another Account");
        }

        if (userRepository.existsByUsername(username)){
            System.out.println("Username Taken!");
            throw new Exception("Username is already being used with another account");
        }

        User createdUser = new User();
        createdUser.setUsername(username);
        createdUser.setEmail(email);
        createdUser.setRole(role);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setElo(elo);

        User savedUser = userRepository.save(createdUser);
        userRepository.save(savedUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(email,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication ,savedUser.getId());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success");
        authResponse.setStatus(true);

        return authResponse;
        }

    @Override
    public AuthResponse signInUser(User loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(username);
        String token = JwtProvider.generateToken(authentication, user.getId()); 
        AuthResponse authResponse = new AuthResponse();

        authResponse.setMessage("Login success");
       
        authResponse.setRole(user.getRole());
        authResponse.setJwt(token);
        authResponse.setStatus(true);
        return authResponse;
    }

    @Override
    public Optional<User> updateUser(Long id, User newUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(newUser.getUsername());
                    user.setElo(newUser.getElo());
                    user.setTournamentsParticipating(newUser.getTournamentsParticipating());
                    return userRepository.save(user);  // Save and return updated user
                });
    }

    @Override
    public User removeUserParticipatingTournament(Long userId, Long tournamentId) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        user.removeParticipatingTournament(currentTournament);  // Remove the tournament from the user
        return userRepository.save(user);  // Save and return the updated user
    }
    @Override
    public List<Long> getUserParticipatingTournaments(Long userId) throws Exception {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        return currentUser.getTournamentsParticipating();  // Return the list of tournaments the user is participating in
    }

    private Authentication authenticate(String username, String password) {

        System.out.println(username+"---++----"+password);

        UserDetails userDetails = loadUserByUsername(username);

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


    @Override
    public List<Tournament> getUserCurrentParticipatingTournament(Long id) {
        List<Long> tournamentIds = userRepository.findById(id)
                .map(User::getTournamentsParticipating)
                .orElse(new ArrayList<>());
        List<Tournament> tournaments = new ArrayList<>();
        for (Long tournamentId : tournamentIds) {
            Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
            if (tournament != null) {
                tournaments.add(tournament);
            }
        }
        return tournaments;
    }
}


