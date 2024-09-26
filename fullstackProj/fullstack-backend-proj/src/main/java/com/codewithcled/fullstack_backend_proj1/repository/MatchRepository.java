package com.codewithcled.fullstack_backend_proj1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codewithcled.fullstack_backend_proj1.model.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {
    
}
