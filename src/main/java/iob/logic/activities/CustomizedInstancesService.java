package iob.logic.activities;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.InstanceIdBoundary;

import java.util.List;

public interface CustomizedInstancesService {
    InstanceBoundary storeInstance(InstanceBoundary instanceBoundary);

    void bindInstances(InstanceIdBoundary parent, InstanceIdBoundary child);

    List<InstanceBoundary> getChildInstancesOfType(InstanceIdBoundary parentId, String type);
}