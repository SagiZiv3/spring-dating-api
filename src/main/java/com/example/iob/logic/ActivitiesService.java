package com.example.iob.logic;

import com.example.iob.boundaries.ActivityBoundary;

import java.util.List;

public interface ActivitiesService {
    Object invokeActivity(ActivityBoundary activity);

    List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail);

    void deleteAllActivities(String adminDomain, String adminEmail);
}