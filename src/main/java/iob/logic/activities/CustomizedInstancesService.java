package iob.logic.activities;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.logic.instancesearching.By;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomizedInstancesService {
    InstanceBoundary store(InstanceBoundary instanceBoundary);

    InstanceBoundary update(InstanceBoundary instanceBoundary);

    void bindInstances(InstanceIdBoundary parent, InstanceIdBoundary child);

    List<InstanceBoundary> findAllEntities(By by, Pageable page);

    InstanceBoundary findEntity(By by);
}