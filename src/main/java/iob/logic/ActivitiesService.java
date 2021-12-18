package iob.logic;

import iob.boundaries.ActivityBoundary;

import java.util.List;

public interface ActivitiesService {
    Object invokeActivity(ActivityBoundary activity);

    @Deprecated
    List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail);

    void deleteAllActivities(String adminDomain, String adminEmail);
}