package com.example.demo.controllers;

import com.example.demo.boundaries.ActivityBoundary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivityController {

    //    @RequestMapping(path = "/iob/activities", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(path = "/iob/activities", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object invokeInstanceActivity(@RequestBody ActivityBoundary activity) {
        ActivityBoundary activity2 = new ActivityBoundary();
        return activity2;
    }
}