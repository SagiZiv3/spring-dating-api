package iob.logic.db;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.converters.ActivityConverter;
import iob.data.ActivityEntity;
import iob.logic.db.dao.ActivitiesDao;
import iob.logic.exceptions.InvalidInputException;
import iob.logic.pagedservices.PagedActivitiesService;
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
        log.info("Validating activity");
        validateActivity(activity);
        ActivityEntity entityToStore = activityConverter.toEntity(activity);
        entityToStore.setCreatedTimestamp(new Date());
        entityToStore.setDomain(domainName);
        log.info("Converted to entityToStore: {}", entityToStore);

        entityToStore = activitiesDao.save(entityToStore);
        log.info("Activity was saved in DB: {}", entityToStore);

        return activityConverter.toBoundary(entityToStore);
    }

    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail) {
        log.error("Called deprecated method");
        throw new RuntimeException("Unimplemented deprecated operation");
    }

    @Override
    @Transactional
    public void deleteAllActivities(String adminDomain, String adminEmail) {
        log.info("Deleting all activities");
        activitiesDao.deleteAll();
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

    @Value("${spring.application.name:dummy}")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Override
    public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail, int page, int size) {
        log.info("Getting {} activities from page {}", size, page);
        Sort.Direction direction = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, direction, "createdTimestamp", "id");

        Page<ActivityEntity> resultPage = this.activitiesDao
                .findAll(pageable);

        log.info("Converting results to boundaries");
        return resultPage
                .stream()
                .map(this.activityConverter::toBoundary)
                .collect(Collectors.toList());
    }
}