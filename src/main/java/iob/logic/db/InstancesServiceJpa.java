package iob.logic.db;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.InitiatedBy;
import iob.boundaries.helpers.UserId;
import iob.data.InstanceEntity;
import iob.data.InstancePrimaryKey;
import iob.logic.InstanceWIthBindingsService;
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
    private final InstanceDao instanceDao;
    private final InstanceConverter instanceConverter;

    @Autowired
    public InstancesServiceJpa(InstanceDao instanceDao, InstanceConverter converter) {
        this.instanceDao = instanceDao;
        this.instanceConverter = converter;
    }

    @Override
    @Transactional
    public InstanceBoundary createInstance(String userDomain, String userEmail, InstanceBoundary instance) {
        instance.setCreatedBy(new InitiatedBy(new UserId(userDomain, userEmail)));
        InstanceEntity entityToStore = instanceConverter.toInstanceEntity(instance);
        entityToStore.setCreatedTimestamp(new Date());
        entityToStore.setDomain(domainName);
//        if (instance.getName().isEmpty() || instance.getType().isEmpty())
//            throw new RuntimeException("Invalid values! name and type can't be null");
        System.out.println(entityToStore);
        entityToStore = instanceDao.save(entityToStore);
        System.out.println(entityToStore);
        return instanceConverter.toInstanceBoundary(entityToStore);
    }

    @Override
    @Transactional
    public InstanceBoundary updateInstance(String userDomain, String userEmail, String instanceDomain, String instanceId, InstanceBoundary update) {
        InstanceEntity existing = this.instanceDao
                .findById(new InstancePrimaryKey(Long.parseLong(instanceId), instanceDomain))
//                .findById(instanceDomain + delimiter + instanceId)
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + instanceId + " in domain " + instanceDomain));

        System.out.println(existing);

        if (update.getActive() != null) {
            existing.setActive(update.getActive());
        }
        if (update.getInstanceAttributes() != null) {
            existing.setInstanceAttributes(update.getInstanceAttributes());
        }
        if (update.getLocation() != null) {
            existing.setLocation(instanceConverter.toLocationEntity(update.getLocation()));
        }
        if (update.getName() != null) {
            existing.setName(update.getName());
        }
        if (update.getType() != null) {
            existing.setType(update.getType());
        }

        existing = instanceDao.save(existing);
        return instanceConverter.toInstanceBoundary(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail) {
        return StreamSupport
                .stream(this.instanceDao
                        .findAll()
                        .spliterator(), false)
                .map(this.instanceConverter::toInstanceBoundary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InstanceBoundary getSpecificInstance(String userDomain, String userEmail, String instanceDomain, String instanceId) {
        InstanceEntity existing = this.instanceDao
                .findById(new InstancePrimaryKey(Long.parseLong(instanceId), instanceDomain))
//                .findById(instanceDomain + delimiter + instanceId)
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + instanceId + " in domain " + instanceDomain));
        return instanceConverter.toInstanceBoundary(existing);
    }

    @Override
    @Transactional
    public void deleteAllInstances(String adminDomain, String adminEmail) {
        instanceDao.deleteAll();
    }

    @Override
    @Transactional
    public void bindToParent(String parentId, String parentDomain, String childId, String childDomain) {
        InstanceEntity parent = this.instanceDao
                .findById(new InstancePrimaryKey(Long.parseLong(parentId), parentDomain))
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + parentId + " in domain " + parentDomain));

        InstanceEntity child = this.instanceDao
                .findById(new InstancePrimaryKey(Long.parseLong(childId), childDomain))
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + childId + " in domain " + childDomain));

        parent.addChild(child);
        child.addParent(parent);
        instanceDao.save(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceBoundary> getParents(String childId, String childDomain) {
        InstanceEntity child = this.instanceDao
                .findById(new InstancePrimaryKey(Long.parseLong(childId), childDomain))
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + childId + " in domain " + childDomain));

        return child.getParentInstances().stream()
                .map(this.instanceConverter::toInstanceBoundary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceBoundary> getChildren(String parentId, String parentDomain) {
        InstanceEntity parent = this.instanceDao
                .findById(new InstancePrimaryKey(Long.parseLong(parentId), parentDomain))
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + parentId + " in domain " + parentDomain));

        return parent.getChildInstances().stream()
                .map(this.instanceConverter::toInstanceBoundary)
                .collect(Collectors.toList());
    }
}