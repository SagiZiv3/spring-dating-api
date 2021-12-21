package iob.logic.pagedservices;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.TimeFrame;
import iob.logic.InstanceWIthBindingsService;

import java.util.List;

public interface PagedInstancesService extends InstanceWIthBindingsService {
    List<InstanceBoundary> getAllInstances(String userDomain, String userEmail, int page, int size);

    List<InstanceBoundary> findByDistance(String userDomain, String userEmail, double centerLat,
                                          double centerLng,
                                          double radius,
                                          int page, int size);

    List<InstanceBoundary> getAllInstancesWithName(String userDomain, String userEmail, String name, int page, int size);

    List<InstanceBoundary> getAllInstancesWithType(String userDomain, String userEmail, String type, int page, int size);

    List<InstanceBoundary> getAllInstancesCreatedInTimeWindow(String userDomain, String userEmail, TimeFrame creationWindow, int page, int size);
}