package com.example.iob.logic;

import com.example.iob.boundaries.InstanceBoundary;
import com.example.iob.boundaries.ObjectId;
import com.example.iob.boundaries.UserId;
import com.example.iob.boundaries.converters.InstanceConverter;
import com.example.iob.data.InstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class InstancesServiceMockup implements InstancesService {
    private final InstanceConverter instanceConverter;
    private Map<String, Map<String, InstanceEntity>> storage;
    @Value("${spring.application.name:dummy}")
    private String domainName;
    private AtomicLong atomicLong;

    @Autowired
    public InstancesServiceMockup(InstanceConverter instanceConverter) {
        this.instanceConverter = instanceConverter;
    }

    @PostConstruct
    private void init() {
        storage = Collections.synchronizedMap(new HashMap<>());
        atomicLong = new AtomicLong(1L);
    }

    @Override
    public InstanceBoundary createInstance(String userDomain, String userEmail, InstanceBoundary instance) {
        Map<String, UserId> createdBy = new HashMap<>();
        createdBy.put("userId", new UserId(userDomain, userEmail));
        instance.setCreatedBy(createdBy);
        instance.setInstanceId(new ObjectId(domainName, atomicLong.getAndIncrement() + ""));
        InstanceEntity entityToStore = instanceConverter.toInstanceEntity(instance);
        entityToStore.setCreatedTimestamp(new Date());
        if (!storage.containsKey(userDomain + userEmail)) {
            storage.putIfAbsent(userDomain + userEmail, Collections.synchronizedMap(new HashMap<>()));
        }
        storage.get(userDomain + userEmail).putIfAbsent(instance.getInstanceId().getDomain() + instance.getInstanceId().getId(), entityToStore);
//        storage.putIfAbsent(userDomain+userEmail, entityToStore);
        return instanceConverter.toInstanceBoundary(entityToStore);
    }

    @Override
    public InstanceBoundary updateInstance(String userDomain, String userEmail, String instanceDomain, String instanceId, InstanceBoundary update) {
        if (storage.containsKey((userDomain + userEmail))) {
            throw new RuntimeException("Couldn't find instance for user with email " + userEmail + " in domain " + userDomain);
        }
        InstanceEntity entity = storage.get(userDomain + userEmail).get(instanceDomain + instanceId);
        boolean isDirty = false;

        if (update.getActive() != null) {
            entity.setActive(update.getActive());
            isDirty = true;
        }
        if (update.getInstanceAttributes() != null) {
            entity.setInstanceAttributes(update.getInstanceAttributes());
            isDirty = true;
        }
        if (update.getLocation() != null) {
            entity.setLocation(instanceConverter.toLocationEntity(update.getLocation()));
            isDirty = true;
        }
        if (update.getName() != null) {
            entity.setName(update.getName());
            isDirty = true;
        }
        if (update.getType() != null) {
            entity.setType(update.getType());
            isDirty = true;
        }

        if (isDirty) {
            storage.get(userDomain + userEmail).put(instanceDomain + instanceId, entity);
            entity = storage.get(userDomain + userEmail).get(instanceDomain + instanceId);
        }
        return instanceConverter.toInstanceBoundary(entity);
    }

    @Override
    public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail) {
        if (storage.containsKey((userDomain + userEmail))) {
            throw new RuntimeException("Couldn't find instance for user with email " + userEmail + " in domain " + userDomain);
        }
        return storage.get(userDomain + userEmail).values()
                .stream()
                .map(instanceConverter::toInstanceBoundary)
                .collect(Collectors.toList());
    }

    @Override
    public InstanceBoundary getSpecificInstance(String userDomain, String userEmail, String instanceDomain, String instanceId) {
        if (storage.containsKey((userDomain + userEmail))) {
            throw new RuntimeException("Couldn't find instance for user with email " + userEmail + " in domain " + userDomain);
        }
        return instanceConverter.toInstanceBoundary(storage.get(userDomain + userEmail).get(instanceDomain + instanceId));
    }

    @Override
    public void deleteAllInstances(String adminDomain, String adminEmail) {
        if (storage.containsKey((adminDomain + adminEmail))) {
            return;
        }
        storage.get(adminDomain + adminEmail).clear();
    }
}