package com.example.demo.controllers;

import com.example.demo.boundaries.NewUserBoundary;
import com.example.demo.boundaries.UserBoundary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    // public static

    @RequestMapping(path = "/iob/users/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary createUser(@RequestBody NewUserBoundary newUser) {
        // returning user boundary
        UserBoundary user = new UserBoundary(newUser);
        return user;
    }

    @RequestMapping(path = "/iob/users/{userDomain}/{userEmail}", method = RequestMethod.PUT)
    public void updateUser(@RequestBody UserBoundary user, @PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
    }

    @RequestMapping(path = "/iob/users/login/{userDomain}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary getUser(@PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String userEmail) {
        // returning user boundary
        UserBoundary user = new UserBoundary("email", "role", "username", "J");
        return user;
    }
}
