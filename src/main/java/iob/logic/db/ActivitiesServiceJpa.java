package iob.logic.db;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.converters.ActivityConverter;
import iob.data.ActivityEntity;
import iob.logic.ActivitiesService;
import iob.logic.db.Daos.ActivitiesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ActivitiesServiceJpa implements ActivitiesService {
    private final ActivityConverter activityConverter;
    private final ActivitiesDao activitiesDao;
    @Value("${spring.application.name:dummy}")
    private String domainName;

    @Autowired
    public ActivitiesServiceJpa(ActivityConverter activityConverter, ActivitiesDao activitiesDao) {
        this.activityConverter = activityConverter;
        this.activitiesDao = activitiesDao;
    }

    @Override
    public Object invokeActivity(ActivityBoundary activity) {
        ActivityEntity entity = activityConverter.toEntity(activity);
        entity.setCreatedTimestamp(new Date());
        entity.setDomain(domainName);

        entity = activitiesDao.save(entity);

        return activityConverter.toBoundary(entity);
    }

    @Override
    public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail) {
        return StreamSupport.stream(
                        activitiesDao.findAll()
                                .spliterator(), false
                ).map(this.activityConverter::toBoundary)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllActivities(String adminDomain, String adminEmail) {
        activitiesDao.deleteAll();
    }
}