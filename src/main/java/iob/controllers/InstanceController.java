package iob.controllers;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.UserIdBoundary;
import iob.logic.pagedservices.PagedInstancesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/iob/instances")
public class InstanceController {
    private final PagedInstancesService instancesService;

    @Autowired
    public InstanceController(PagedInstancesService instancesService) {
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
                                                  @PathVariable("userEmail") String userEmail,
                                                  @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                  @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return instancesService.getAllInstances(userDomain, userEmail, page, size);
    }

    @GetMapping(path = "/{userDomain}/{userEmail}/search/byName/{name}")
    public List<InstanceBoundary> getAllInstancesWithName(@PathVariable("userDomain") String userDomain,
                                                          @PathVariable("userEmail") String userEmail,
                                                          @PathVariable("name") String name,
                                                          @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                          @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return Collections.emptyList();
    }

    @GetMapping(path = "/{userDomain}/{userEmail}/search/byType/{type}")
    public List<InstanceBoundary> getAllInstancesWithType(@PathVariable("userDomain") String userDomain,
                                                          @PathVariable("userEmail") String userEmail,
                                                          @PathVariable("type") String type,
                                                          @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                          @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return Collections.emptyList();
    }

    @GetMapping(path = "/{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}")
    public List<InstanceBoundary> getAllInstancesNearLocation(@PathVariable("userDomain") String userDomain,
                                                              @PathVariable("userEmail") String userEmail,
                                                              @PathVariable("lat") double lat,
                                                              @PathVariable("lng") double lng,
                                                              @PathVariable("distance") double distance,
                                                              @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                              @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return instancesService.findByDistance(lat, lng, distance, page, size);
    }

    @GetMapping(path = "/{userDomain}/{userEmail}/search/created/{creationWindow}")
    public List<InstanceBoundary> getAllInstancesCreatedInTimeWindow(@PathVariable("userDomain") String userDomain,
                                                                     @PathVariable("userEmail") String userEmail,
                                                                     @PathVariable("creationWindow") Date creationWindow,
                                                                     @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                                     @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return Collections.emptyList();
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