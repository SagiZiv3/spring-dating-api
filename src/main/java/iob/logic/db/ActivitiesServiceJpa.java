package iob.logic.db;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.converters.ActivityConverter;
import iob.data.ActivityEntity;
import iob.logic.UserPermissionsHandler;
import iob.logic.activities.InvokableActivity;
import iob.logic.annotations.ParameterType;
import iob.logic.annotations.RoleParameter;
import iob.logic.annotations.RoleRestricted;
import iob.logic.annotations.UserRoleParameter;
import iob.logic.db.dao.ActivitiesDao;
import iob.logic.exceptions.InvalidInputException;
import iob.logic.pagedservices.PagedActivitiesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
    //<editor-fold desc="Class variables">
    private final ActivityConverter activityConverter;
    private final ActivitiesDao activitiesDao;
    private final UserPermissionsHandler userPermissionsHandler;
    private final ApplicationContext applicationContext;
    private String domainName;
    //</editor-fold>

    @Autowired
    public ActivitiesServiceJpa(ActivityConverter activityConverter, ActivitiesDao activitiesDao, UserPermissionsHandler userPermissionsHandler, ApplicationContext applicationContext) {
        this.activityConverter = activityConverter;
        this.activitiesDao = activitiesDao;
        this.userPermissionsHandler = userPermissionsHandler;
        this.applicationContext = applicationContext;
    }

    //<editor-fold desc="Get methods">
    @Override
    @RoleRestricted(permittedRoles = UserRoleParameter.ADMIN)
    public List<ActivityBoundary> getAllActivities(@RoleParameter(parameterType = ParameterType.DOMAIN) String adminDomain,
                                                   @RoleParameter(parameterType = ParameterType.EMAIL) String adminEmail,
                                                   int page, int size) {
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
    //</editor-fold>

    //<editor-fold desc="Modification methods (create/delete)">
    @Override
    @Transactional
    public Object invokeActivity(ActivityBoundary activity) {
        // Make sure that the user is actually a player.
        userPermissionsHandler.throwIfNotAuthorized(activity.getInvokedBy().getUserId().getDomain(),
                activity.getInvokedBy().getUserId().getEmail(), UserRoleParameter.PLAYER);
        saveEntity(activity);

        return applicationContext.getBean(activity.getType(), InvokableActivity.class)
                .invoke(activity);
    }

    private void saveEntity(ActivityBoundary activity) {
        log.info("Validating activity");
        validateActivity(activity);
        ActivityEntity entityToStore = activityConverter.toEntity(activity);
        entityToStore.setCreatedTimestamp(new Date());
        entityToStore.setDomain(domainName);
        log.info("Converted to an entity: {}", entityToStore);

        entityToStore = activitiesDao.save(entityToStore);
        log.info("Activity was saved in DB: {}", entityToStore);
    }

    @Override
    @Transactional
    @RoleRestricted(permittedRoles = UserRoleParameter.ADMIN)
    public void deleteAllActivities(@RoleParameter(parameterType = ParameterType.DOMAIN) String adminDomain,
                                    @RoleParameter(parameterType = ParameterType.EMAIL) String adminEmail) {
        log.info("Deleting all activities");
        activitiesDao.deleteAll();
    }
    //</editor-fold>

    //<editor-fold desc="Helper methods">

    private void validateActivity(ActivityBoundary activity) {
        if (StringUtils.isBlank(activity.getType())) {
            throw new InvalidInputException("type", activity.getType());
        }
        if (activity.getInvokedBy() == null) {
            throw new InvalidInputException("invoked by", null);
        }
        if (activity.getInstance() == null) {
            throw new InvalidInputException("instance", null);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Deprecated methods">
    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail) {
        log.error("Called deprecated method");
        throw new RuntimeException("Unimplemented deprecated operation");
    }
    //</editor-fold>

    @Value("${spring.application.name:dummy}")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
}