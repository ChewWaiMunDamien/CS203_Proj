package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.User;
import org.springframework.stereotype.Service;


import java.util.List;


public interface UserService {


    public List<User> getAllUser()  ;

    public User findUserProfileByJwt(String jwt);

    public User findUserByEmail(String email) ;

    public User findUserById(String userId) ;

    public List<User> findAllUsers();

    public User loadByUsername(String username);

    public void updateUserElo(long uAId,long uBId,int elo1,int elo2);
}