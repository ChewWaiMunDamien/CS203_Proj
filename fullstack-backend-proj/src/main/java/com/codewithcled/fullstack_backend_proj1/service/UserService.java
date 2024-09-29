package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


public interface UserService {


    public List<User> getAllUser()  ;

    public User findUserProfileByJwt(String jwt);

    public User findUserByEmail(String email) ;

    public User findUserById(String userId) ;

    public List<User> findAllUsers();
    public List<UserDTO> findAllUsersDTO();
    public List<UserDTO> getUserChanges();
    public User loadByUsername(String username);

    public AuthResponse createUser(User user) throws Exception;

    public AuthResponse signInUser(User loginRequest);

    public Optional<User> updateUser(Long id, User newUser);

    public User removeUserParticipatingTournament(Long userId, Long tournamentId) throws Exception;
    public List<Long> getUserParticipatingTournaments(Long userId) throws Exception;

    public List<Tournament> getUserCurrentParticipatingTournament(Long id);


}