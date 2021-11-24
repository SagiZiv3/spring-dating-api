package iob.logic;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.helpers.ObjectId;
import iob.boundaries.converters.ActivityConverter;
import iob.data.ActivityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ActivitiesServiceMockup implements ActivitiesService {
    private final ActivityConverter activityConverter;
    private Map<String, ActivityEntity> storage;
    @Value("${spring.application.name:dummy}")
    private String domainName;
    private AtomicLong atomicLong;

    @Autowired
    public ActivitiesServiceMockup(ActivityConverter activityConverter) {
        this.activityConverter = activityConverter;
    }


    @PostConstruct
    private void init() {
        storage = Collections.synchronizedMap(new HashMap<>());
        atomicLong = new AtomicLong(1L);
    }

    @Override
    public Object invokeActivity(ActivityBoundary activity) {
        activity.setActivityId(new ObjectId(domainName, atomicLong.getAndIncrement() + ""));
        activity.setCreatedTimestamp(new Date());
        ActivityEntity entity = activityConverter.toActivityEntity(activity);

        storage.putIfAbsent(entity.getActivityId(), entity);

        return activityConverter.toActivityBoundary(entity);
    }

    @Override
    public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail) {
        return storage.values()
                .stream()
                .map(activityConverter::toActivityBoundary)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllActivities(String adminDomain, String adminEmail) {
        storage.clear();
    }
}