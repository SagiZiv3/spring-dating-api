package iob.logic.db;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.InitiatedBy;
import iob.boundaries.helpers.ObjectId;
import iob.boundaries.helpers.UserId;
import iob.data.InstanceEntity;
import iob.logic.InstancesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class InstancesServiceJpa implements InstancesService {
    @Value("${spring.application.name:dummy}")
    private String domainName;
    @Value("${application.entity.delimiter}")
    private String delimiter;
    private final InstanceDao instanceDao;
    private final InstanceConverter instanceConverter;
    private AtomicLong atomicLong;

    @Autowired
    public InstancesServiceJpa(InstanceDao instanceDao, InstanceConverter converter) {
        this.instanceDao = instanceDao;
        this.instanceConverter = converter;
    }

    @PostConstruct
    private void init() {
        atomicLong = new AtomicLong(1L);
    }

    @Override
    public InstanceBoundary createInstance(String userDomain, String userEmail, InstanceBoundary instance) {
        instance.setCreatedBy(new InitiatedBy(new UserId(userDomain, userEmail)));
        instance.setInstanceId(new ObjectId(domainName, Long.toString(atomicLong.getAndIncrement())));
        InstanceEntity entityToStore = instanceConverter.toInstanceEntity(instance);
        entityToStore.setCreatedTimestamp(new Date());
        entityToStore = instanceDao.save(entityToStore);
        return instanceConverter.toInstanceBoundary(entityToStore);
    }

    @Override
    public InstanceBoundary updateInstance(String userDomain, String userEmail, String instanceDomain, String instanceId, InstanceBoundary update) {
        InstanceEntity existing = this.instanceDao
                .findById(instanceDomain + delimiter + instanceId)
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + instanceId + " in domain " + instanceDomain));

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
    public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail) {
        return StreamSupport
                .stream(this.instanceDao
                        .findAll()
                        .spliterator(), false)
                .map(this.instanceConverter::toInstanceBoundary)
                .collect(Collectors.toList());
    }

    @Override
    public InstanceBoundary getSpecificInstance(String userDomain, String userEmail, String instanceDomain, String instanceId) {
        InstanceEntity existing = this.instanceDao
                .findById(instanceDomain + delimiter + instanceId)
                .orElseThrow(() -> new RuntimeException("Couldn't find instance for user with id " + instanceId + " in domain " + instanceDomain));
        return instanceConverter.toInstanceBoundary(existing);
    }

    @Override
    public void deleteAllInstances(String adminDomain, String adminEmail) {
        instanceDao.deleteAll();
    }
}