package iob.logic;

import iob.boundaries.InstanceBoundary;

import java.util.List;

public interface InstanceWIthBindingsService extends InstancesService {
    void bindToParent(String userDomain, String userEmail, String parentId, String parentDomain, String childId, String childDomain);

    List<InstanceBoundary> getParents(String userDomain, String userEmail, String childId, String childDomain);

    List<InstanceBoundary> getChildren(String userDomain, String userEmail, String parentId, String parentDomain);
}