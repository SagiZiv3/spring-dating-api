package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.boundaries.InstanceBoundary;
import com.example.demo.boundaries.UserBoundary;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// test upload 
@RestController
public class InstanceController {

    @RequestMapping(path = "/iob/instances/{userDomain}/{userEmail}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public InstanceBoundary createInstance(@RequestBody InstanceBoundary newInstance,
            @PathVariable("userDomain") String userDomain, @PathVariable("userEmail") String userEmail) {
        InstanceBoundary instance = new InstanceBoundary(newInstance);

        HashMap<String, String> instanceId = new HashMap<String, String>();
        instanceId.put("domain", userDomain);
        instanceId.put("id", Long.toString(InstanceBoundary.getId()));

        UserBoundary user = new UserBoundary();
        HashMap<String, String> userId = new HashMap<String, String>();
        userId.put("domain", userDomain);
        userId.put("email", userEmail);

        user.setUserId(userId);

        HashMap<String, Map<String, String>> createdBy = new HashMap<String, Map<String, String>>();
        createdBy.put("createdBy", user.getUserId());

        instance.setCreatedBy(createdBy);

        instance.setInstanceId(instanceId);
        return instance;
    }

    @RequestMapping(path = "/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}", method = RequestMethod.PUT)
    public void updateInstance(@RequestBody InstanceBoundary newInstance, @PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String instanceDomain,
            @PathVariable("instanceId") String instanceId) {
        // here should be an update for the user instance
    }

    @RequestMapping(path = "/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public InstanceBoundary retrieveInstance(@PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String instanceDomain,
            @PathVariable("instanceId") String instanceId) {
        InstanceBoundary instance = new InstanceBoundary();
        return instance;

    }

    @RequestMapping(path = "/iob/instances/{userDomain}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<InstanceBoundary> getAllInstances(@PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String useEmail) {
        InstanceBoundary instance1 = new InstanceBoundary();
        InstanceBoundary instance2 = new InstanceBoundary();
        ArrayList<InstanceBoundary> arr = new ArrayList<InstanceBoundary>();
        arr.add(instance1);
        arr.add(instance2);
        return arr;

    }

}
