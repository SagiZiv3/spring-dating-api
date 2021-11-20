package com.example.demo.controllers;

import com.example.demo.boundaries.ActivityBoundary;
import com.example.demo.logic.ActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/iob/activities")
public class ActivityController {
    private final ActivitiesService activitiesService;

    @Autowired
    public ActivityController(ActivitiesService activitiesService) {
        this.activitiesService = activitiesService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object invokeInstanceActivity(@RequestBody ActivityBoundary activity) {
        return activitiesService.invokeActivity(activity);
    }
}