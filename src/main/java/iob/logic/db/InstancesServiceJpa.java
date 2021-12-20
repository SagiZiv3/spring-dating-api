package iob.logic.db;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.data.InstanceEntity;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class InstancesServiceJpa implements PagedInstancesService {
    private String domainName;
    private final InstancesDao instancesDao;
    private final InstanceConverter instanceConverter;

    @Autowired
    public InstancesServiceJpa(InstancesDao instancesDao, InstanceConverter converter) {
        this.instancesDao = instancesDao;
        this.instanceConverter = converter;
    }

    @Override
    @Transactional
    @RoleRestricted(permittedRoles = {UserRoleParameter.MANAGER, UserRoleParameter.PLAYER})
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
    public InstanceBoundary updateInstance(String userDomain, String userEmail, String instanceDomain, String instanceId, InstanceBoundary update) {
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
    @Transactional(readOnly = true)
    @Deprecated
    public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail) {
        log.error("Called deprecated method");
        throw new RuntimeException("Unimplemented deprecated operation");
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail, int page, int size) {
        log.info("Getting {} instances from page {}", size, page);
        Sort.Direction direction = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, direction, "createdTimestamp", "id");

        Page<InstanceEntity> resultPage = this.instancesDao
                .findAll(pageable);

        log.info("Converting results to boundaries");
        return resultPage
                .stream()
                .map(this.instanceConverter::toBoundary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InstanceBoundary getSpecificInstance(String userDomain, String userEmail, String instanceDomain, String instanceId) {
        InstanceEntity existing = findInstance(instanceDomain, instanceId);
        return instanceConverter.toBoundary(existing);
    }

    @Override
    @Transactional
    @RoleRestricted(permittedRoles = UserRoleParameter.ADMIN)
    public void deleteAllInstances(@RoleParameter(parameterType = ParameterType.DOMAIN) String adminDomain,
                                   @RoleParameter(parameterType = ParameterType.EMAIL) String adminEmail) {
        instancesDao.deleteAll();
    }

    private InstanceEntity findInstance(String domain, String id) {
        return this.instancesDao
                .findById(instanceConverter.toInstancePrimaryKey(id, domain))
                .orElseThrow(() -> new InstanceNotFoundException(domain, id));
    }

    private void validateInstance(InstanceBoundary instance) {
        if (instance.getName() == null || instance.getName().isEmpty())
            throw new InvalidInputException("name", instance.getName());

        if (instance.getType() == null || instance.getType().isEmpty())
            throw new InvalidInputException("type", instance.getType());
    }

    @Override
    @Transactional
    public void bindToParent(String parentId, String parentDomain, String childId, String childDomain) {
        InstanceEntity parent = findInstance(parentDomain, parentId);
        InstanceEntity child = findInstance(childDomain, childId);

        parent.addChild(child);
        child.addParent(parent);
        instancesDao.save(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceBoundary> getParents(String childId, String childDomain) {
        InstanceEntity child = findInstance(childDomain, childId);

        return child.getParentInstances().stream()
                .map(this.instanceConverter::toBoundary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceBoundary> getChildren(String parentId, String parentDomain) {
        InstanceEntity parent = findInstance(parentDomain, parentId);

        return parent.getChildInstances().stream()
                .map(this.instanceConverter::toBoundary)
                .collect(Collectors.toList());
    }

    @Value("${spring.application.name:dummy}")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
}