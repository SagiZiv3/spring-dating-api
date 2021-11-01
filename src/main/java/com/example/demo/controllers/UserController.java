package com.example.demo.controllers;

import com.example.demo.boundaries.UserBoundary;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class UserController {
    // public static

    @RequestMapping(path = "/iob/users/login/{userDomain}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary getUser(@PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String useEmail) {
        // returning user boundary

        UserBoundary user = new UserBoundary("email", "role", "username", "J");
        return user;
    }

    @PostMapping(value = "/iob/users/", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary createUser(@RequestBody String email, @RequestBody String role, @RequestBody String username,
            @RequestBody String avatar) {
        // returning user boundary
        UserBoundary user = new UserBoundary(email, role, username, avatar);
        return user;
    }
}
