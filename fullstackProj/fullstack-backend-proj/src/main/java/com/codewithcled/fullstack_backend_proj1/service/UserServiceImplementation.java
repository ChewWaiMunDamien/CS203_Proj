package com.codewithcled.fullstack_backend_proj1.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import com.codewithcled.fullstack_backend_proj1.model.User;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImplementation implements UserService,UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository=userRepository;
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
    public User loadByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    @Transactional
    public void updateUserElo(long uAId,long uBId,int elo1,int elo2){
        if (elo1<0 || elo2<0){
            throw new IllegalArgumentException("Invalid Elo ratings");
        }

        if (!userRepository.existsById(uAId) || !userRepository.existsById(uBId)){
            throw new IllegalArgumentException("Cannot find Users");
        }

        userRepository.findById(uAId).map(user -> {
            user.setElo(elo1);;
            return userRepository.save(user);
        }).orElseThrow(() -> new EntityNotFoundException("User A not found"));

        userRepository.findById(uBId).map(user -> {
            user.setElo(elo2);;
            return userRepository.save(user);
        }).orElseThrow(() -> new EntityNotFoundException("User B not found"));
    }
    
}