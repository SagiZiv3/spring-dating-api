package com.example.demo.controllers;

import com.example.demo.boundaries.ActivityBoundary;
import com.example.demo.boundaries.UserBoundary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class AdminController {

//    @RequestMapping(path = "/iob/admin/users/{userDomain}/{userEmail}", method = RequestMethod.DELETE)
    @DeleteMapping(path = "/iob/admin/users/{userDomain}/{userEmail}")
    public void deleteAllUsers(@PathVariable("userDomain") String userDomain,
                               @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
    }

//    @RequestMapping(path = "/iob/admin/users/{userDomain}/{userEmail}", method = RequestMethod.GET)
    @GetMapping(path = "/iob/admin/users/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserBoundary> exportAllUsers(@PathVariable("userDomain") String userDomain,
                                             @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
        UserBoundary user1 = new UserBoundary();
        UserBoundary user2 = new UserBoundary();
        List<UserBoundary> arr = Arrays.asList(user1, user2);
        return arr;
    }

//    @RequestMapping(path = "/iob/admin/activities/{userDomain}/{userEmail}", method = RequestMethod.GET)
    @GetMapping(path = "/iob/admin/activities/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ActivityBoundary> exportAllActivities(@PathVariable("userDomain") String userDomain,
                                                      @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
        ActivityBoundary activity1 = new ActivityBoundary();
        ActivityBoundary activity2 = new ActivityBoundary();
        List<ActivityBoundary> arr = Arrays.asList(activity1, activity2);
        return arr;
    }

//    @RequestMapping(path = "/iob/admin/instances/{userDomain}/{userEmail}", method = RequestMethod.DELETE)
    @DeleteMapping(path = "/iob/admin/instances/{userDomain}/{userEmail}")
    public void deleteAllInstances(@PathVariable("userDomain") String userDomain,
                                   @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
    }

//    @RequestMapping(path = "/iob/admin/activities/{userDomain}/{userEmail}", method = RequestMethod.DELETE)
    public void deleteAllActivities(@PathVariable("userDomain") String userDomain,
                                    @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
    }
}