package iob.controllers;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.TimeFrame;
import iob.boundaries.helpers.UserIdBoundary;
import iob.logic.SearchableInstancesService;
import iob.logic.instancesearching.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = URLS.INSTANCES.ROOT)
public class InstanceController {
    private final SearchableInstancesService instancesService;

    @Autowired
    public InstanceController(SearchableInstancesService instancesService) {
        this.instancesService = instancesService;
    }

    //<editor-fold desc="Get requests">
    @GetMapping(path = URLS.INSTANCES.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public InstanceBoundary retrieveInstance(@PathVariable("userDomain") String userDomain,
                                             @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String instanceDomain,
                                             @PathVariable("instanceId") String instanceId) {
        return instancesService.getSpecificInstance(userDomain, useEmail, instanceDomain, instanceId);

    }

    @GetMapping(path = URLS.INSTANCES.GET_ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<InstanceBoundary> getAllInstances(@PathVariable("userDomain") String userDomain,
                                                  @PathVariable("userEmail") String userEmail,
                                                  @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                  @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return instancesService.getAllInstances(userDomain, userEmail, page, size);
    }

    @GetMapping(path = URLS.INSTANCES.GET_CHILDREN, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<InstanceBoundary> getAllInstanceChildren(@PathVariable("userDomain") String userDomain,
                                                         @PathVariable("userEmail") String userEmail,
                                                         @PathVariable String instanceDomain, @PathVariable String instanceId,
                                                         @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return instancesService.findAllEntities(By.childOf(instanceDomain, instanceId), userDomain, userEmail, page, size);
    }

    @GetMapping(path = URLS.INSTANCES.GET_PARENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<InstanceBoundary> getAllInstanceParents(@PathVariable("userDomain") String userDomain,
                                                        @PathVariable("userEmail") String userEmail,
                                                        @PathVariable String instanceDomain, @PathVariable String instanceId,
                                                        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return instancesService.findAllEntities(By.parentOf(instanceDomain, instanceId), userDomain, userEmail, page, size);
    }
    //</editor-fold>

    //<editor-fold desc="Find requests">
    @GetMapping(path = URLS.INSTANCES.FIND_BY_NAME)
    public List<InstanceBoundary> getAllInstancesWithName(@PathVariable("userDomain") String userDomain,
                                                          @PathVariable("userEmail") String userEmail,
                                                          @PathVariable("name") String name,
                                                          @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                          @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return instancesService.findAllEntities(By.name(name), userDomain, userEmail, page, size);
        //        return instancesService.getAllInstancesWithName(userDomain, userEmail, name, page, size);
    }

    @GetMapping(path = URLS.INSTANCES.FIND_BY_TYPE)
    public List<InstanceBoundary> getAllInstancesWithType(@PathVariable("userDomain") String userDomain,
                                                          @PathVariable("userEmail") String userEmail,
                                                          @PathVariable("type") String type,
                                                          @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                          @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return instancesService.findAllEntities(By.type(type), userDomain, userEmail, page, size);
//        return instancesService.getAllInstancesWithType(userDomain, userEmail, type, page, size);
    }

    @GetMapping(path = URLS.INSTANCES.FIND_BY_LOCATION)
    public List<InstanceBoundary> getAllInstancesNearLocation(@PathVariable("userDomain") String userDomain,
                                                              @PathVariable("userEmail") String userEmail,
                                                              @PathVariable("lat") double lat,
                                                              @PathVariable("lng") double lng,
                                                              @PathVariable("distance") double distance,
                                                              @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                              @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return instancesService.findAllEntities(By.distance(lat, lng, distance), userDomain, userEmail, page, size);
    }

    @GetMapping(path = URLS.INSTANCES.FIND_BY_DATE)
    public List<InstanceBoundary> getAllInstancesCreatedInTimeWindow(@PathVariable("userDomain") String userDomain,
                                                                     @PathVariable("userEmail") String userEmail,
                                                                     @PathVariable("creationWindow") TimeFrame creationWindow,
                                                                     @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                                     @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return instancesService.findAllEntities(By.creationDate(creationWindow), userDomain, userEmail, page, size);
    }
    //</editor-fold>

    //<editor-fold desc="Post requests">
    @PostMapping(path = URLS.INSTANCES.CREATE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InstanceBoundary> createInstance(@RequestBody InstanceBoundary newInstance,
                                                           @PathVariable("userDomain") String userDomain, @PathVariable("userEmail") String userEmail,
                                                           UriComponentsBuilder uriComponentsBuilder) {
        CreatedByBoundary creatorOfInstance = new CreatedByBoundary(new UserIdBoundary(userDomain, userEmail));
        newInstance.setCreatedBy(creatorOfInstance);

        InstanceBoundary instance = instancesService.createInstance(userDomain, userEmail, newInstance);

        URI instanceUri = uriComponentsBuilder.path(URLS.INSTANCES.ROOT + URLS.INSTANCES.GET)
                .buildAndExpand(userDomain, userEmail,
                        instance.getInstanceId().getDomain(), instance.getInstanceId().getId()).toUri();
        return ResponseEntity.created(instanceUri).body(instance);
    }
    //</editor-fold>

    //<editor-fold desc="Put requests">
    @PutMapping(path = URLS.INSTANCES.UPDATE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateInstance(@RequestBody InstanceBoundary update, @PathVariable("userDomain") String userDomain,
                               @PathVariable("userEmail") String useEmail, @PathVariable("instanceDomain") String instanceDomain,
                               @PathVariable("instanceId") String instanceId) {
        instancesService.updateInstance(userDomain, useEmail, instanceDomain, instanceId, update);
    }

    @PutMapping(path = URLS.INSTANCES.BIND)
    public void bindToChild(
            @RequestBody InstanceIdBoundary childInstanceId, @PathVariable("userDomain") String userDomain,
            @PathVariable("userEmail") String userEmail, @PathVariable("instanceDomain") String parentInstanceDomain,
            @PathVariable("instanceId") String parentInstanceId) {
        instancesService.bindToParent(userDomain, userEmail, parentInstanceId, parentInstanceDomain, childInstanceId.getId(), childInstanceId.getDomain());
    }
    //</editor-fold>

}