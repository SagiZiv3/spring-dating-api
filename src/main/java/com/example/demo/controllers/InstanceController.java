package com.example.demo.controllers;

import com.example.demo.boundaries.InstanceBoundary;
import com.example.demo.logic.InstancesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/iob/instances")
public class InstanceController {
    private final InstancesService instancesService;

    @Autowired
    public InstanceController(InstancesService instancesService) {
        this.instancesService = instancesService;
    }

    @PostMapping(path = "/{userDomain}/{userEmail}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public InstanceBoundary createInstance(@RequestBody InstanceBoundary newInstance,
                                           @PathVariable("userDomain") String userDomain, @PathVariable("userEmail") String userEmail) {
        return instancesService.createInstance(userDomain, userEmail, newInstance);
    }

    @PutMapping(path = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateInstance(@RequestBody InstanceBoundary update, @PathVariable("userDomain") String userDomain,
                               @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String instanceDomain,
                               @PathVariable("instanceId") String instanceId) {
        instancesService.updateInstance(userDomain, useEmail, instanceDomain, instanceId, update);
    }

    @GetMapping(path = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public InstanceBoundary retrieveInstance(@PathVariable("userDomain") String userDomain,
                                             @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String instanceDomain,
                                             @PathVariable("instanceId") String instanceId) {
        return instancesService.getSpecificInstance(userDomain, useEmail, instanceDomain, instanceId);

    }

    @GetMapping(path = "/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<InstanceBoundary> getAllInstances(@PathVariable("userDomain") String userDomain,
                                                  @PathVariable("userEmail") String userEmail) {
        return instancesService.getAllInstances(userDomain, userEmail);

    }

}