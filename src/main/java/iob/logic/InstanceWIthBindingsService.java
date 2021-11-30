package iob.logic;

import iob.boundaries.InstanceBoundary;

import java.util.List;

public interface InstanceWIthBindingsService extends InstancesService {
    void bindToParent(String parentId, String parentDomain, String childId, String childDomain);

    List<InstanceBoundary> getParents(String childId, String childDomain);

    List<InstanceBoundary> getChildren(String parentId, String parentDomain);
}