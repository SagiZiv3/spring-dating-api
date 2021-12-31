package iob.logic.activities;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.logic.instancesearching.By;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomizedInstancesService {

    Optional<InstanceBoundary> findEntity(By by);

    List<InstanceBoundary> findAllEntities(By by, Pageable page);

    InstanceBoundary store(InstanceBoundary instanceBoundary);

    InstanceBoundary update(InstanceBoundary instanceBoundary);

    void bindInstances(InstanceIdBoundary parent, InstanceIdBoundary child);
}