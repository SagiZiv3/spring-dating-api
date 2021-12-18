package iob.logic.db;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.converters.ActivityConverter;
import iob.data.ActivityEntity;
import iob.logic.db.dao.ActivitiesDao;
import iob.logic.exceptions.InvalidInputException;
import iob.logic.pagedservices.PagedActivitiesService;
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
public class ActivitiesServiceJpa implements PagedActivitiesService {
    private final ActivityConverter activityConverter;
    private final ActivitiesDao activitiesDao;
    private String domainName;

    @Autowired
    public ActivitiesServiceJpa(ActivityConverter activityConverter, ActivitiesDao activitiesDao) {
        this.activityConverter = activityConverter;
        this.activitiesDao = activitiesDao;
    }

    @Override
    @Transactional
    public Object invokeActivity(ActivityBoundary activity) {
        validateActivity(activity);
        ActivityEntity entity = activityConverter.toEntity(activity);
        entity.setCreatedTimestamp(new Date());
        entity.setDomain(domainName);

        entity = activitiesDao.save(entity);

        return activityConverter.toBoundary(entity);
    }

    private void validateActivity(ActivityBoundary activity) {
        if (activity.getType() == null || activity.getType().isEmpty()) {
            throw new InvalidInputException("type", activity.getType());
        }
        if (activity.getInvokedBy() == null) {
            throw new InvalidInputException("invoked by", null);
        }
        if (activity.getInstance() == null) {
            throw new InvalidInputException("instance", null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail) {
        throw new RuntimeException("Unimplemented deprecated operation");
    }

    @Override
    public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail, int page, int size) {
        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction, "createdTimestamp", "id");

        Page<ActivityEntity> resultPage = this.activitiesDao
                .findAll(pageable);

        return resultPage
                .stream()
                .map(this.activityConverter::toBoundary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllActivities(String adminDomain, String adminEmail) {
        activitiesDao.deleteAll();
    }

    @Value("${spring.application.name:dummy}")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
}