package com.example.demo.controllers;

import com.example.demo.boundaries.NewUserBoundary;
import com.example.demo.boundaries.UserBoundary;
import com.example.demo.boundaries.UserId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/iob/users/")
public class UserController {
    @Value("${spring.application.name:dummy}")
    private String applicationDomainName;

    //    @RequestMapping(path = "/iob/users/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary createUser(@RequestBody NewUserBoundary newUser) {
        // returning user boundary
        UserBoundary user = new UserBoundary();
        user.setUserId(new UserId(applicationDomainName, newUser.getEmail())); // Create the user with the app's domain
        user.setUsername(newUser.getUsername());
        user.setRole(newUser.getRole());
        user.setAvatar(newUser.getAvatar());
        return user;
    }

    //    @RequestMapping(path = "/iob/users/{userDomain}/{userEmail}", method = RequestMethod.PUT)
    @PutMapping(path = "/{userDomain}/{userEmail}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@RequestBody UserBoundary user, @PathVariable("userDomain") String userDomain,
                           @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
    }

    //    @RequestMapping(path = "/iob/users/login/{userDomain}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(path = "/login/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary getUser(@PathVariable("userDomain") String userDomain,
                                @PathVariable("userEmail") String userEmail) {
        // returning user boundary
//        UserBoundary user = new UserBoundary("email", UserRole.PLAYER, "username", "J");
        return null;
    }
}