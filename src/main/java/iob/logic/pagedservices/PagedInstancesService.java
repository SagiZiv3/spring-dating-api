package iob.logic.pagedservices;

import iob.boundaries.InstanceBoundary;
import iob.logic.InstanceWIthBindingsService;

import java.util.List;

public interface PagedInstancesService extends InstanceWIthBindingsService {
    List<InstanceBoundary> getAllInstances(String userDomain, String userEmail, int page, int size);
}