package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.boundaries.ActivityBoundary;
import com.example.demo.boundaries.InstanceBoundary;
import com.example.demo.boundaries.UserBoundary;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @RequestMapping(path = "/iob/admin/users/{userDomain}/{userEmail}", method = RequestMethod.DELETE)
    public void deleteAllUsers(@PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
    }

    @RequestMapping(path = "/iob/admin/users/{userDomain}/{userEmail}", method = RequestMethod.GET)
    public List<UserBoundary> exportAllUsers(@PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
        UserBoundary user1 = new UserBoundary();
        UserBoundary user2 = new UserBoundary();
        List<UserBoundary> arr = Arrays.asList(user1, user2);
        return arr;
    }

    @RequestMapping(path = "/iob/admin/activities/{userDomain}/{userEmail}", method = RequestMethod.GET)
    public List<ActivityBoundary> exportAllActivities(@PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
        ActivityBoundary activity1 = new ActivityBoundary();
        ActivityBoundary activity2 = new ActivityBoundary();
        List<ActivityBoundary> arr = Arrays.asList(activity1, activity2);
        return arr;
    }

    @RequestMapping(path = "/iob/admin/instances/{userDomain}/{userEmail}", method = RequestMethod.DELETE)
    public void deleteAllInstances(@PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
    }

    @RequestMapping(path = "/iob/admin/activities/{userDomain}/{userEmail}", method = RequestMethod.DELETE)
    public void deleteAllActivities(@PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String userEmail) {
        // here should be an update of the user and returning nothing
    }
}
