package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.codewithcled.fullstack_backend_proj1.service.EloRatingServiceImplementation;




@RestController
public class EloRatingController {
    
    @Autowired
    private EloRatingServiceImplementation EloRatingService;


    @GetMapping("/rating/{s1}/{s2}/{o}/{k}")
    double EloRatingCalculation(@PathVariable("s1") int score1, @PathVariable("s2") int score2,@PathVariable("o") int outcome,@PathVariable("k") int k) {
        return EloRatingService.EloCalculation(score1, score2, outcome,k);
    }
    

}
