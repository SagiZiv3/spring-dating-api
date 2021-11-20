package com.example.iob.controllers;

import com.example.iob.boundaries.ActivityBoundary;
import com.example.iob.boundaries.UserBoundary;
import com.example.iob.logic.ActivitiesService;
import com.example.iob.logic.InstancesService;
import com.example.iob.logic.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/iob/admin")
public class AdminController {
    private final UsersService usersService;
    private final InstancesService instancesService;
    private final ActivitiesService activitiesService;

    @Autowired
    public AdminController(UsersService usersService, InstancesService instancesService, ActivitiesService activitiesService) {
        this.usersService = usersService;
        this.instancesService = instancesService;
        this.activitiesService = activitiesService;
    }

    @GetMapping(path = "/users/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserBoundary> exportAllUsers(@PathVariable("userDomain") String adminDomain,
                                             @PathVariable("userEmail") String adminEmail) {
        return usersService.getAllUsers(adminDomain, adminEmail);
    }

    @GetMapping(path = "/iob/admin/activities/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ActivityBoundary> exportAllActivities(@PathVariable("userDomain") String adminDomain,
                                                      @PathVariable("userEmail") String adminEmail) {
        return activitiesService.getAllActivities(adminDomain, adminEmail);
    }

    @DeleteMapping(path = "/users/{userDomain}/{userEmail}")
    public void deleteAllUsers(@PathVariable("userDomain") String adminDomain,
                               @PathVariable("userEmail") String adminEmail) {

        usersService.deleteAllUsers(adminDomain, adminEmail);
    }

    @DeleteMapping(path = "/instances/{userDomain}/{userEmail}")
    public void deleteAllInstances(@PathVariable("userDomain") String adminDomain,
                                   @PathVariable("userEmail") String adminEmail) {
        instancesService.deleteAllInstances(adminDomain, adminEmail);
    }

    @DeleteMapping(path = "/activities/{userDomain}/{userEmail}")
    public void deleteAllActivities(@PathVariable("userDomain") String adminDomain,
                                    @PathVariable("userEmail") String adminEmail) {
        activitiesService.deleteAllActivities(adminDomain, adminEmail);
    }
}