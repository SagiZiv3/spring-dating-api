package iob.logic.db;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.converters.IdsConverter;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.InitiatedBy;
import iob.boundaries.helpers.UserId;
import iob.data.InstanceEntity;
import iob.data.primarykeys.InstancePrimaryKey;
import iob.logic.InstanceWIthBindingsService;
import iob.logic.db.Daos.InstancesDao;
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
    private final IdsConverter idsConverter;


    @Autowired
    public InstancesServiceJpa(InstancesDao instancesDao, InstanceConverter converter, IdsConverter idsConverter) {
        this.instancesDao = instancesDao;
        this.instanceConverter = converter;
        this.idsConverter = idsConverter;
    }

    @Override
    @Transactional
    public InstanceBoundary createInstance(String userDomain, String userEmail, InstanceBoundary instance) {
        InstanceEntity entityToStore = instanceConverter.toInstanceEntity(instance);
        entityToStore.setCreatedTimestamp(new Date());
        entityToStore.setDomain(domainName);
        entityToStore.setCreatedBy(idsConverter.toUserIdMapEntity(new InitiatedBy(new UserId(userDomain, userEmail))));

        if (instance.getName() == null || instance.getName().isEmpty() ||
                instance.getType() == null || instance.getType().isEmpty())
            throw new RuntimeException("Invalid values! name and type can't be null");

        entityToStore = instancesDao.save(entityToStore);

        return instanceConverter.toInstanceBoundary(entityToStore);
    }

    @Override
    @Transactional
    public InstanceBoundary updateInstance(String userDomain, String userEmail, String instanceDomain, String instanceId, InstanceBoundary update) {
        InstanceEntity existing = this.instancesDao
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
        if (update.getName() != null && !update.getName().isEmpty()) {
            existing.setName(update.getName());
        }
        if (update.getType() != null && !update.getType().isEmpty()) {
            existing.setType(update.getType());
        }

        existing = instancesDao.save(existing);
        return instanceConverter.toInstanceBoundary(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail) {
        return StreamSupport
                .stream(this.instancesDao
                        .findAll()
                        .spliterator(), false)
                .map(this.instanceConverter::toInstanceBoundary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InstanceBoundary getSpecificInstance(String userDomain, String userEmail, String instanceDomain, String instanceId) {
        InstanceEntity existing = this.instancesDao
                .findById(new InstancePrimaryKey(Long.parseLong(instanceId), instanceDomain))
//                .findById(instanceDomain + delimiter + instanceId)
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + instanceId + " in domain " + instanceDomain));
        return instanceConverter.toInstanceBoundary(existing);
    }

    @Override
    @Transactional
    public void deleteAllInstances(String adminDomain, String adminEmail) {
        instancesDao.deleteAll();
    }

    @Override
    @Transactional
    public void bindToParent(String parentId, String parentDomain, String childId, String childDomain) {
        InstanceEntity parent = this.instancesDao
                .findById(new InstancePrimaryKey(Long.parseLong(parentId), parentDomain))
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + parentId + " in domain " + parentDomain));

        InstanceEntity child = this.instancesDao
                .findById(new InstancePrimaryKey(Long.parseLong(childId), childDomain))
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + childId + " in domain " + childDomain));

        parent.addChild(child);
        child.addParent(parent);
        instancesDao.save(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceBoundary> getParents(String childId, String childDomain) {
        InstanceEntity child = this.instancesDao
                .findById(new InstancePrimaryKey(Long.parseLong(childId), childDomain))
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + childId + " in domain " + childDomain));

        return child.getParentInstances().stream()
                .map(this.instanceConverter::toInstanceBoundary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstanceBoundary> getChildren(String parentId, String parentDomain) {
        InstanceEntity parent = this.instancesDao
                .findById(new InstancePrimaryKey(Long.parseLong(parentId), parentDomain))
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + parentId + " in domain " + parentDomain));

        return parent.getChildInstances().stream()
                .map(this.instanceConverter::toInstanceBoundary)
                .collect(Collectors.toList());
    }
}