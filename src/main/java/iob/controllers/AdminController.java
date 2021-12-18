package iob.controllers;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.UserBoundary;
import iob.logic.InstancesService;
import iob.logic.UsersService;
import iob.logic.pagedservices.PagedActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/iob/admin")
public class AdminController {
    private final UsersService usersService;
    private final InstancesService instancesService;
    private final PagedActivitiesService activitiesService;

    @Autowired
    public AdminController(UsersService usersService, InstancesService instancesService, PagedActivitiesService activitiesService) {
        this.usersService = usersService;
        this.instancesService = instancesService;
        this.activitiesService = activitiesService;
    }

    @GetMapping(path = "/users/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserBoundary> exportAllUsers(@PathVariable("userDomain") String adminDomain,
                                             @PathVariable("userEmail") String adminEmail) {
        return usersService.getAllUsers(adminDomain, adminEmail);
    }

    @GetMapping(path = "/activities/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ActivityBoundary> exportAllActivities(@PathVariable("userDomain") String adminDomain,
                                                      @PathVariable("userEmail") String adminEmail,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                      @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return activitiesService.getAllActivities(adminDomain, adminEmail, page, size);
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