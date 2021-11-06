package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ActivityController {

    @RequestMapping(path = "/iob/activities", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object invokeInstanceActivity(@RequestBody ActivityBoundary activity) {
        ActivityBoundary activity2 = new ActivityBoundary();
        return activity2;
    }
}
