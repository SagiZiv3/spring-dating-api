package iob.controllers;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.UserIdBoundary;
import iob.logic.InstanceWIthBindingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/iob/instances")
public class InstanceController {
    private final InstanceWIthBindingsService instancesService;

    @Autowired
    public InstanceController(InstanceWIthBindingsService instancesService) {
        this.instancesService = instancesService;
    }

    @PostMapping(path = "/{userDomain}/{userEmail}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public InstanceBoundary createInstance(@RequestBody InstanceBoundary newInstance,
                                           @PathVariable("userDomain") String userDomain, @PathVariable("userEmail") String userEmail) {
        CreatedByBoundary creatorOfInstance = new CreatedByBoundary(new UserIdBoundary(userDomain, userEmail));
        newInstance.setCreatedBy(creatorOfInstance);

        return instancesService.createInstance(userDomain, userEmail, newInstance);
    }

    @PutMapping(path = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateInstance(@RequestBody InstanceBoundary update, @PathVariable("userDomain") String userDomain,
                               @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String instanceDomain,
                               @PathVariable("instanceId") String instanceId) {
        instancesService.updateInstance(userDomain, useEmail, instanceDomain, instanceId, update);
    }

    @PutMapping(path = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/children")
    public void bindToChild(
            @RequestBody InstanceIdBoundary childInstanceId, @PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String parentInstanceDomain,
            @PathVariable("instanceId") String parentInstanceId) {
        instancesService.bindToParent(parentInstanceId, parentInstanceDomain, childInstanceId.getId(), childInstanceId.getDomain());
    }

    @GetMapping(path = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public InstanceBoundary retrieveInstance(@PathVariable("userDomain") String userDomain,
                                             @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String instanceDomain,
                                             @PathVariable("instanceId") String instanceId) {
        return instancesService.getSpecificInstance(userDomain, useEmail, instanceDomain, instanceId);

    }

    @GetMapping(path = "/{userDomain}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<InstanceBoundary> getAllInstances(@PathVariable("userDomain") String userDomain,
                                                  @PathVariable("userEmail") String userEmail) {
        return instancesService.getAllInstances(userDomain, userEmail);

    }

    @GetMapping(path = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/children", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<InstanceBoundary> getAllInstanceChildren(@PathVariable("userDomain") String userDomain,
                                                         @PathVariable("userEmail") String userEmail,
                                                         @PathVariable String instanceDomain, @PathVariable String instanceId) {
        return instancesService.getChildren(instanceId, instanceDomain);

    }

    @GetMapping(path = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/parents", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<InstanceBoundary> getAllInstanceParents(@PathVariable("userDomain") String userDomain,
                                                        @PathVariable("userEmail") String userEmail,
                                                        @PathVariable String instanceDomain, @PathVariable String instanceId) {
        return instancesService.getParents(instanceId, instanceDomain);

    }

}