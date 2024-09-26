package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@RestController
public class TestController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserServiceImplementation userService;


    private static final String baseURL="http://localhost:8080";
    private static final String serviceURL="/rating";

    @GetMapping("/testCall")
    Integer tesInteger() {
        return callElo(800,900,-1,20);
    }

    @PutMapping("/testUpdate/{id1}/{id2}/{rating1}/{rating2}")
    public void testUpdate(@PathVariable long id1,@PathVariable long id2,@PathVariable int rating1,@PathVariable int rating2) {
        //Match match = matchRepository.findById(id);
        
        userService.updateUserElo(id1, id2, rating1, rating2);
    }

    private Integer callElo(int eloA,int eloB,int outcome,int kValue){
        StringBuilder url = new StringBuilder();
        url.append(baseURL);
        url.append(serviceURL);
        url.append("/"+eloA);
        url.append("/"+eloB);
        url.append("/"+outcome);
        url.append("/"+kValue);
        return restTemplate.getForObject(url.toString(), Integer.class);
    }
}
