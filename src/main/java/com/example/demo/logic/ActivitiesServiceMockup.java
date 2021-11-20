package com.example.demo.logic;

import com.example.demo.boundaries.ActivityBoundary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivitiesServiceMockup implements ActivitiesService {
    @Override
    public Object invokeActivity(ActivityBoundary activity) {
        return null;
    }

    @Override
    public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail) {
        return null;
    }

    @Override
    public void deleteAllActivities(String adminDomain, String adminEmail) {

    }
}