package iob.controllers;

import iob.boundaries.NewUserBoundary;
import iob.boundaries.UserBoundary;
import iob.boundaries.converters.UserConverter;
import iob.logic.UsersService;
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
@RequestMapping(path = "/iob/users")
public class UserController {
    private final UsersService usersService;
    private final UserConverter userConverter;

    @Autowired
    public UserController(UsersService usersService, UserConverter userConverter) {
        this.usersService = usersService;
        this.userConverter = userConverter;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary createUser(@RequestBody NewUserBoundary newUser) {
        UserBoundary userBoundary = userConverter.toBoundary(newUser);
        return usersService.createUser(userBoundary);
    }

    @PutMapping(path = "/{userDomain}/{userEmail}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@RequestBody UserBoundary user, @PathVariable("userDomain") String userDomain,
                           @PathVariable("userEmail") String userEmail) {
        usersService.updateUser(userDomain, userEmail, user);
    }

    @GetMapping(path = "/login/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary getUser(@PathVariable("userDomain") String userDomain,
                                @PathVariable("userEmail") String userEmail) {
        return usersService.login(userDomain, userEmail);
    }
}