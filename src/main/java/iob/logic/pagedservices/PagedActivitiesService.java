package iob.logic.pagedservices;

import iob.boundaries.ActivityBoundary;
import iob.logic.ActivitiesService;

import java.util.List;

public interface PagedActivitiesService extends ActivitiesService {
    List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail, int page, int size);
}