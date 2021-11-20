package com.example.demo.logic;

import com.example.demo.boundaries.InstanceBoundary;

import java.util.List;

public interface InstancesService {
    InstanceBoundary createInstance(String userDomain, String userEmail, InstanceBoundary instance);

    InstanceBoundary updateInstance(String userDomain, String userEmail, String instanceDomain, String instanceId, InstanceBoundary update);

    List<InstanceBoundary> getAllInstances(String userDomain, String userEmail);

    InstanceBoundary getSpecificInstance(String userDomain, String userEmail, String instanceDomain, String instanceId);

    void deleteAllInstances(String adminDomain, String adminEmail);
}