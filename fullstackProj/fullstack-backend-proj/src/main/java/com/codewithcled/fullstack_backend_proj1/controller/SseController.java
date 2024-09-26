package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.service.UserService;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;

import java.util.List;
import java.time.Duration;

import reactor.core.publisher.Flux;


@RestController
public class SseController {
    
    @Autowired
    UserServiceImplementation userService;
    private final int timer = 5;

    @CrossOrigin(allowedHeaders = "*")
    @GetMapping(value="/sse/users",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<User>> getMethodName() {
        return Flux.interval(Duration.ofSeconds(timer))
        .map(tick -> userService.findAllUsers());
    }
    
}
