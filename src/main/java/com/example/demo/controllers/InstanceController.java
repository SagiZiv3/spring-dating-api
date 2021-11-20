package com.example.demo.controllers;

import com.example.demo.boundaries.InstanceBoundary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping(path = "/iob/instances")
public class InstanceController {

    //    @RequestMapping(path = "/iob/instances/{userDomain}/{userEmail}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(path = "/{userDomain}/{userEmail}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public InstanceBoundary createInstance(@RequestBody InstanceBoundary newInstance,
                                           @PathVariable("userDomain") String userDomain, @PathVariable("userEmail") String userEmail) {
//        InstanceBoundary instance = new InstanceBoundary(newInstance);
//
//        HashMap<String, String> instanceId = new HashMap<>();
//        instanceId.put("domain", userDomain);
//        instanceId.put("id", Long.toString(InstanceBoundary.getId()));
//
//        UserBoundary user = new UserBoundary();
//        UserId userId = new UserId(userDomain, userEmail);
//        user.setUserId(userId);
//
//        HashMap<String, UserId> createdBy = new HashMap<>();
//        createdBy.put("createdBy", user.getUserId());
//
//        instance.setCreatedBy(createdBy);
//
//        instance.setInstanceId(instanceId);
        return null;
    }

    //    @RequestMapping(path = "/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}", method = RequestMethod.PUT)
    @PutMapping(path = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}")
    public void updateInstance(@RequestBody InstanceBoundary newInstance, @PathVariable("userDomain") String userDomain,
                               @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String instanceDomain,
                               @PathVariable("instanceId") String instanceId) {
        // here should be an update for the user instance
    }

    //    @RequestMapping(path = "/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(path = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public InstanceBoundary retrieveInstance(@PathVariable("userDomain") String userDomain,
                                             @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String instanceDomain,
                                             @PathVariable("instanceId") String instanceId) {
        InstanceBoundary instance = new InstanceBoundary();
        return instance;

    }

    //    @RequestMapping(path = "/iob/instances/{userDomain}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(path = "/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<InstanceBoundary> getAllInstances(@PathVariable("userDomain") String userDomain,
                                                       @PathVariable("userEmail") String useEmail) {
        InstanceBoundary instance1 = new InstanceBoundary();
        InstanceBoundary instance2 = new InstanceBoundary();
        ArrayList<InstanceBoundary> arr = new ArrayList<>();
        arr.add(instance1);
        arr.add(instance2);
        return arr;

    }

}