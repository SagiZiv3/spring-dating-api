package iob.logic.db;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.data.InstanceEntity;
import iob.logic.SearchableInstancesService;
import iob.logic.UserPermissionsHandler;
import iob.logic.activities.CustomizedInstancesService;
import iob.logic.annotations.ParameterType;
import iob.logic.annotations.RoleParameter;
import iob.logic.annotations.RoleRestricted;
import iob.logic.annotations.UserRoleParameter;
import iob.logic.db.dao.InstancesDao;
import iob.logic.exceptions.InvalidInputException;
import iob.logic.exceptions.instance.InstanceNotFoundException;
import iob.logic.instancesearching.By;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Optional;

@Service
@Slf4j
public class InstancesServiceJpa implements SearchableInstancesService, CustomizedInstancesService {
    //<editor-fold desc="Class variables">
    private String domainName;
    private final InstancesDao instancesDao;
    private final InstanceConverter instanceConverter;
    private final UserPermissionsHandler permissionsHandler;
    //</editor-fold>

    @Autowired
    public InstancesServiceJpa(InstancesDao instancesDao, InstanceConverter converter, UserPermissionsHandler permissionsHandler) {
        this.instancesDao = instancesDao;
        this.instanceConverter = converter;
        this.permissionsHandler = permissionsHandler;
    }

    //<editor-fold desc="Get methods">
    @Override
    @Transactional(readOnly = true)
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
    public List<InstanceBoundary> getAllInstances(@RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                                  @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                                  int page, int size) {
        log.info("Getting {} instances from page {}", size, page);
        Pageable pageable = getDefaultPageable(page, size);

        Page<InstanceEntity> resultPage = this.instancesDao.findAllByActiveIn(
                permissionsHandler.getAllowedActiveStatesForUser(userDomain, userEmail),
                pageable);

        log.info("Converting results to boundaries");
        return instanceConverter.toBoundaries(resultPage.getContent());
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
        return store(instance);
    }

    @Override
    public InstanceBoundary store(InstanceBoundary instanceBoundary) {
        validateInstance(instanceBoundary);
        InstanceEntity entityToStore = instanceConverter.toEntity(instanceBoundary);
        entityToStore.setCreatedTimestamp(new Date());
        entityToStore.setDomain(domainName);
        log.info("Converted to entity: {}", entityToStore);

        entityToStore = instancesDao.save(entityToStore);
        log.info("Saved instance: {}", entityToStore);

        return instanceConverter.toBoundary(entityToStore);
    }

    @Override
    public void bindInstances(InstanceIdBoundary parent, InstanceIdBoundary child) {
        InstanceEntity parentEntity = findInstance(parent.getDomain(), parent.getId());
        InstanceEntity childEntity = findInstance(child.getDomain(), child.getId());

        parentEntity.addChild(childEntity);
        childEntity.addParent(parentEntity);
        instancesDao.save(parentEntity);
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

    @Override
    public InstanceBoundary update(InstanceBoundary instanceBoundary) {
        InstanceEntity entity = instanceConverter.toEntity(instanceBoundary);
        entity = instancesDao.save(entity);
        return instanceConverter.toBoundary(entity);
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
        if (StringUtils.isBlank(instance.getName()))
            throw new InvalidInputException("name", instance.getName());

        if (StringUtils.isBlank(instance.getType()))
            throw new InvalidInputException("type", instance.getType());
    }

    //<editor-fold desc="Helper methods">
    private InstanceEntity findInstance(String domain, String id) {
        return this.instancesDao
                .findById(instanceConverter.toInstancePrimaryKey(id, domain))
                .orElseThrow(() -> new InstanceNotFoundException(domain, id));
    }

    private static @NonNull Pageable getDefaultPageable(int page, int size) {
        Sort.Direction direction = Sort.Direction.DESC;
        return PageRequest.of(page, size, direction, "createdTimestamp", "id");
    }
    //</editor-fold>

    @Value("${spring.application.name:dummy}")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Override
    public List<InstanceBoundary> findAllEntities(By by, Pageable page) {
        return instanceConverter.toBoundaries(
                instancesDao.findAll(by.getQuery(), page)
                        .getContent()
        );
    }

    @Override
    public Optional<InstanceBoundary> findEntity(By by) {
        Optional<InstanceEntity> instanceEntity = instancesDao.findOne(by.getQuery());
        return instanceEntity.map(instanceConverter::toBoundary);
    }

    @Override
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
    public List<InstanceBoundary> findAllEntities(By by,
                                                  @RoleParameter(parameterType = ParameterType.DOMAIN) String userDomain,
                                                  @RoleParameter(parameterType = ParameterType.EMAIL) String userEmail,
                                                  int page, int size) {
        by = by.and(By.activeIn(permissionsHandler.getAllowedActiveStatesForUser(userDomain, userEmail)));
        return findAllEntities(by, getDefaultPageable(page, size));
    }
}