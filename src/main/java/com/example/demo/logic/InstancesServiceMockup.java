package com.example.demo.logic;

import com.example.demo.boundaries.InstanceBoundary;
import com.example.demo.boundaries.UserId;
import com.example.demo.boundaries.converters.InstanceConverter;
import com.example.demo.data.InstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InstancesServiceMockup implements InstancesService {
    private final InstanceConverter instanceConverter;
    private Map<String, InstanceEntity> storage;

    @Autowired
    public InstancesServiceMockup(InstanceConverter instanceConverter) {
        this.instanceConverter = instanceConverter;
    }

    @Override
    public InstanceBoundary createInstance(String userDomain, String userEmail, InstanceBoundary instance) {
        Map<String, UserId> createdBy = new HashMap<>();
        createdBy.put("userId", new UserId(userDomain, userEmail));
        instance.setCreatedBy(createdBy);
        InstanceEntity entityToStore = instanceConverter.toInstanceEntity(instance);
        entityToStore.setCreatedTimestamp(new Date());
        if (storage.containsKey(entityToStore.getInstanceId())) {
            throw new RuntimeException("Instance already exists with id " + instance.getInstanceId().getId() +
                    " in domain " + instance.getInstanceId().getDomain());
        }
        storage.putIfAbsent(entityToStore.getInstanceId(), entityToStore);
        return instanceConverter.toInstanceBoundary(entityToStore);
    }

    @Override
    public InstanceBoundary updateInstance(String userDomain, String userEmail, String instanceDomain, String instanceId, InstanceBoundary update) {
        return null;
    }

    @Override
    public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail) {
        return null;
    }

    @Override
    public InstanceBoundary getSpecificInstance(String userDomain, String userEmail, String instanceDomain, String instanceId) {
        return null;
    }

    @Override
    public void deleteAllInstances(String adminDomain, String adminEmail) {

    }
}