package iob.logic.db;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.TimeFrame;
import iob.data.InstanceEntity;
import iob.data.TimeFrameEntity;
import iob.logic.annotations.ParameterType;
import iob.logic.annotations.RoleParameter;
import iob.logic.annotations.RoleRestricted;
import iob.logic.annotations.UserRoleParameter;
import iob.logic.db.dao.InstancesDao;
import iob.logic.exceptions.InvalidInputException;
import iob.logic.exceptions.instance.InstanceNotFoundException;
import iob.logic.pagedservices.PagedInstancesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class InstancesServiceJpa implements PagedInstancesService {
    //<editor-fold desc="Class variables">
    private String domainName;
    private final InstancesDao instancesDao;
    private final InstanceConverter instanceConverter;
    //</editor-fold>

    @Autowired
    public InstancesServiceJpa(InstancesDao instancesDao, InstanceConverter converter) {
        this.instancesDao = instancesDao;
        this.instanceConverter = converter;
    }

    //<editor-fold desc="Get methods">

    @Override
    @Transactional(readOnly = true)
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
    public List<InstanceBoundary> getParents(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                             @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                             String childId, String childDomain) {
        InstanceEntity child = findInstance(childDomain, childId);
        return instanceConverter.toBoundaries(child.getParentInstances());
    }

    @Override
    @Transactional(readOnly = true)
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
    public List<InstanceBoundary> getChildren(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                              @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                              String parentId, String parentDomain) {
        InstanceEntity parent = findInstance(parentDomain, parentId);

        return instanceConverter.toBoundaries(parent.getChildInstances());
    }

    @Override
    @Transactional(readOnly = true)
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
    public List<InstanceBoundary> getAllInstances(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                                  @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                                  int page, int size) {
        log.info("Getting {} instances from page {}", size, page);
        Pageable pageable = getDefaultPageable(page, size);

        Page<InstanceEntity> resultPage = this.instancesDao.findAll(pageable);

        log.info("Converting results to boundaries");
        return instanceConverter.toBoundaries(resultPage.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
    public List<InstanceBoundary> findByDistance(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                                 @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                                 double centerLat,
                                                 double centerLng,
                                                 double radius,
                                                 int page, int size) {
        log.debug("Lat: {}, Lng: {}, Radius: {}", centerLat, centerLng, radius);
        Pageable pageable = getDefaultPageable(page, size);
        return instanceConverter.toBoundaries(instancesDao.getAllEntitiesInRadiusAndActive(centerLat,
                centerLng, radius, true, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
    public List<InstanceBoundary> getAllInstancesWithName(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                                          @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                                          String name, int page, int size) {
        Pageable pageable = getDefaultPageable(page, size);
        return instanceConverter.toBoundaries(instancesDao.findAllByNameAndActiveEquals(name, true, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
    public List<InstanceBoundary> getAllInstancesWithType(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                                          @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                                          String type, int page, int size) {
        Pageable pageable = getDefaultPageable(page, size);
        return instanceConverter.toBoundaries(instancesDao.findAllByTypeAndActiveEquals(type, true, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
    public List<InstanceBoundary> getAllInstancesCreatedInTimeWindow(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                                                     @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                                                     TimeFrame creationWindow, int page, int size) {
        Pageable pageable = getDefaultPageable(page, size);
        TimeFrameEntity timeFrame = TimeFrameEntity.valueOf(creationWindow.name());

        log.info("Getting all instance between {} and {}, based on {}",
                timeFrame.getStartDate(), timeFrame.getEndDate(), timeFrame);

        return instanceConverter.toBoundaries(instancesDao.findAllByCreatedTimestampBetweenAndActiveEquals(
                timeFrame.getStartDate(), timeFrame.getEndDate(), true, pageable));
    }

    //<editor-fold desc="Deprecated methods">
    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail) {
        log.error("Called deprecated method");
        throw new RuntimeException("Unimplemented deprecated operation");
    }

    //</editor-fold>

    //<editor-fold desc="Modification methods (create/update/delete/bind)">
    @Override
    @Transactional
    @RoleRestricted(permittedRoles = UserRoleParameter.MANAGER)
    public InstanceBoundary createInstance(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                           @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                           InstanceBoundary instance) {
        log.info("Validating instance");
        validateInstance(instance);
        InstanceEntity entityToStore = instanceConverter.toEntity(instance);
        entityToStore.setCreatedTimestamp(new Date());
        entityToStore.setDomain(domainName);
        log.info("Converted to entity: {}", entityToStore);

        entityToStore = instancesDao.save(entityToStore);
        log.info("Saved instance: {}", entityToStore);

        return instanceConverter.toBoundary(entityToStore);
    }

    @Override
    @Transactional
    @RoleRestricted(permittedRoles = UserRoleParameter.MANAGER)
    public InstanceBoundary updateInstance(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                           @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                           String instanceDomain, String instanceId, InstanceBoundary update) {
        log.info("Searching instance in DB");
        InstanceEntity entity = findInstance(instanceDomain, instanceId);

        log.info("Updating instance's data");
        if (update.getActive() != null) {
            entity.setActive(update.getActive());
        }
        if (update.getInstanceAttributes() != null) {
            entity.setInstanceAttributes(update.getInstanceAttributes());
        }
        if (update.getLocation() != null) {
            entity.setLocation(instanceConverter.toLocationEntity(update.getLocation()));
        }
        if (update.getName() != null && !update.getName().isEmpty()) {
            entity.setName(update.getName());
        }
        if (update.getType() != null && !update.getType().isEmpty()) {
            entity.setType(update.getType());
        }

        entity = instancesDao.save(entity);
        log.info("Modified instance was saved in DB: {}", entity);
        return instanceConverter.toBoundary(entity);
    }

    @Override
    @Transactional
    @RoleRestricted(permittedRoles = UserRoleParameter.ADMIN)
    public void deleteAllInstances(@RoleParameter(parameterType = ParameterType.DOMAIN) String adminDomain,
                                   @RoleParameter(parameterType = ParameterType.EMAIL) String adminEmail) {
        instancesDao.deleteAll();
    }

    @Override
    @Transactional
    @RoleRestricted(permittedRoles = UserRoleParameter.MANAGER)
    public void bindToParent(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                             @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                             String parentId, String parentDomain, String childId, String childDomain) {
        InstanceEntity parent = findInstance(parentDomain, parentId);
        InstanceEntity child = findInstance(childDomain, childId);

        parent.addChild(child);
        child.addParent(parent);
        instancesDao.save(parent);
    }
    //</editor-fold>

    @Override
    @Transactional(readOnly = true)
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
    public InstanceBoundary getSpecificInstance(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                                @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                                String instanceDomain, String instanceId) {
        InstanceEntity existing = findInstance(instanceDomain, instanceId);
        return instanceConverter.toBoundary(existing);
    }

    private void validateInstance(InstanceBoundary instance) {
        if (instance.getName() == null || instance.getName().isEmpty())
            throw new InvalidInputException("name", instance.getName());

        if (instance.getType() == null || instance.getType().isEmpty())
            throw new InvalidInputException("type", instance.getType());
    }
    //</editor-fold>

    //<editor-fold desc="Helper methods">
    private InstanceEntity findInstance(String domain, String id) {
        return this.instancesDao
                .findById(instanceConverter.toInstancePrimaryKey(id, domain))
                .orElseThrow(() -> new InstanceNotFoundException(domain, id));
    }

    private static Pageable getDefaultPageable(int page, int size) {
        Sort.Direction direction = Sort.Direction.DESC;
        return PageRequest.of(page, size, direction, "createdTimestamp", "id");
    }
    //</editor-fold>

    @Value("${spring.application.name:dummy}")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
}