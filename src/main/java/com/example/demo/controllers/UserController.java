package com.example.demo.controllers;

import com.example.demo.boundaries.NewUserBoundary;
import com.example.demo.boundaries.UserBoundary;
import com.example.demo.boundaries.converters.UserConverter;
import com.example.demo.logic.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UsersService usersService;
    private final UserConverter userConverter;

    @Autowired
    public UserController(UsersService usersService, UserConverter userConverter) {
        this.usersService = usersService;
        this.userConverter = userConverter;
    }

    //    @RequestMapping(path = "/iob/users/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary createUser(@RequestBody NewUserBoundary newUser) {
        UserBoundary userBoundary = userConverter.toUserBoundary(newUser);
        return usersService.createUser(userBoundary);
    }

    //    @RequestMapping(path = "/iob/users/{userDomain}/{userEmail}", method = RequestMethod.PUT)
    @PutMapping(path = "/{userDomain}/{userEmail}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@RequestBody UserBoundary user, @PathVariable("userDomain") String userDomain,
                           @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
        usersService.updateUser(userDomain, userEmail, user);
    }

    //    @RequestMapping(path = "/iob/users/login/{userDomain}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(path = "/login/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary getUser(@PathVariable("userDomain") String userDomain,
                                @PathVariable("userEmail") String userEmail) {
        // returning user boundary
//        UserBoundary user = new UserBoundary("email", UserRole.PLAYER, "username", "J");
        return usersService.getAllUsers(userDomain, userEmail).get(0);
    }
}