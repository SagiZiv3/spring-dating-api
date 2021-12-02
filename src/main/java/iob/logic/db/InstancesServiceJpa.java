package iob.logic.db;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.data.InstanceEntity;
import iob.logic.InstanceWIthBindingsService;
import iob.logic.db.Daos.InstancesDao;
import iob.logic.exceptions.InvalidInputException;
import iob.logic.exceptions.instance.InstanceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class InstancesServiceJpa implements InstanceWIthBindingsService {
    @Value("${spring.application.name:dummy}")
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
    public InstanceBoundary createInstance(String userDomain, String userEmail, InstanceBoundary instance) {
        validateInstance(instance);
        InstanceEntity entityToStore = instanceConverter.toEntity(instance);
        entityToStore.setCreatedTimestamp(new Date());
        entityToStore.setDomain(domainName);

        entityToStore = instancesDao.save(entityToStore);

        return instanceConverter.toBoundary(entityToStore);
    }

    @Override
    @Transactional
    public InstanceBoundary updateInstance(String userDomain, String userEmail, String instanceDomain, String instanceId, InstanceBoundary update) {
        InstanceEntity existing = findInstance(instanceDomain, instanceId);

        if (update.getActive() != null) {
            existing.setActive(update.getActive());
        }
        if (update.getInstanceAttributes() != null) {
            existing.setInstanceAttributes(update.getInstanceAttributes());
        }
        if (update.getLocation() != null) {
            existing.setLocation(instanceConverter.toLocationEntity(update.getLocation()));
        }
        if (update.getName() != null && !update.getName().isEmpty()) {
            existing.setName(update.getName());
        }
        if (update.getType() != null && !update.getType().isEmpty()) {
            existing.setType(update.getType());
        }

        existing = instancesDao.save(existing);
        return instanceConverter.toBoundary(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail) {
        return StreamSupport
                .stream(this.instancesDao
                        .findAll()
                        .spliterator(), false)
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
    public void deleteAllInstances(String adminDomain, String adminEmail) {
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
}