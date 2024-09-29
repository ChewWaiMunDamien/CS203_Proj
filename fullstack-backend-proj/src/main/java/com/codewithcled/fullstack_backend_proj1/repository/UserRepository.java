package com.codewithcled.fullstack_backend_proj1.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import  com.codewithcled.fullstack_backend_proj1.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findByUsername(String username);


    @Query("SELECT u FROM User u WHERE u.lastModified > :timestamp")
    List<User> findChangesSince(@Param("timestamp") LocalDateTime timestamp);

}
