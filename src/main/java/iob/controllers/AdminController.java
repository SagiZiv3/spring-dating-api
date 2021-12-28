package iob.controllers;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.UserBoundary;
import iob.logic.InstancesService;
import iob.logic.pagedservices.PagedActivitiesService;
import iob.logic.pagedservices.PagedUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = URLS.ADMIN.ROOT)
public class AdminController {
    private final PagedUsersService usersService;
    private final InstancesService instancesService;
    private final PagedActivitiesService activitiesService;

    @Autowired
    public AdminController(PagedUsersService usersService, InstancesService instancesService, PagedActivitiesService activitiesService) {
        this.usersService = usersService;
        this.instancesService = instancesService;
        this.activitiesService = activitiesService;
    }

    //<editor-fold desc="Get requests">
    @GetMapping(path = URLS.ADMIN.EXPORT_USERS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserBoundary> exportAllUsers(@PathVariable("userDomain") String adminDomain,
                                             @PathVariable("userEmail") String adminEmail,
                                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                             @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return usersService.getAllUsers(adminDomain, adminEmail, page, size);
    }

    @GetMapping(path = URLS.ADMIN.EXPORT_ACTIVITIES, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ActivityBoundary> exportAllActivities(@PathVariable("userDomain") String adminDomain,
                                                      @PathVariable("userEmail") String adminEmail,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                      @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return activitiesService.getAllActivities(adminDomain, adminEmail, page, size);
    }
    //</editor-fold>

    //<editor-fold desc="Delete requests">
    @DeleteMapping(path = URLS.ADMIN.DELETE_USERS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllUsers(@PathVariable("userDomain") String adminDomain,
                               @PathVariable("userEmail") String adminEmail) {

        usersService.deleteAllUsers(adminDomain, adminEmail);
    }

    @DeleteMapping(path = URLS.ADMIN.DELETE_INSTANCES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllInstances(@PathVariable("userDomain") String adminDomain,
                                   @PathVariable("userEmail") String adminEmail) {
        instancesService.deleteAllInstances(adminDomain, adminEmail);
    }

    @DeleteMapping(path = URLS.ADMIN.DELETE_ACTIVITIES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllActivities(@PathVariable("userDomain") String adminDomain,
                                    @PathVariable("userEmail") String adminEmail) {
        activitiesService.deleteAllActivities(adminDomain, adminEmail);
    }
    //</editor-fold>
}