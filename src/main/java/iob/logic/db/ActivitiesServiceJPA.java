package iob.logic.db;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.converters.ActivityConverter;
import iob.data.ActivityEntity;
import iob.logic.ActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ActivitiesServiceJPA implements ActivitiesService {
    private final ActivityConverter activityConverter;
    private final ActivitiesDao activitiesDao;
    @Value("${spring.application.name:dummy}")
    private String domainName;

    @Autowired
    public ActivitiesServiceJPA(ActivityConverter activityConverter, ActivitiesDao activitiesDao) {
        this.activityConverter = activityConverter;
        this.activitiesDao = activitiesDao;
    }

    @Override
    public Object invokeActivity(ActivityBoundary activity) {
        System.out.println("INVOKE");
        ActivityEntity entity = activityConverter.toActivityEntity(activity);
        System.out.println(entity);
        entity.setCreatedTimestamp(new Date());
        entity.setDomain(domainName);
        System.out.println("A  " + entity);

        entity = activitiesDao.save(entity);
        System.out.println("B  " + entity);

        return activityConverter.toActivityBoundary(entity);
    }

    @Override
    public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail) {
        return StreamSupport.stream(
                        activitiesDao.findAll()
                                .spliterator(), false
                ).map(this.activityConverter::toActivityBoundary)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllActivities(String adminDomain, String adminEmail) {
        activitiesDao.deleteAll();
    }
}