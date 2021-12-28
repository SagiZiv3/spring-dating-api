package iob.logic;

import iob.boundaries.InstanceBoundary;
import iob.logic.instancesearching.By;
import iob.logic.pagedservices.PagedInstancesService;

import java.util.List;

public interface SearchableInstancesService extends PagedInstancesService {
    List<InstanceBoundary> findAllEntities(By by, String userDomain, String userEmail, int page, int size);
}