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
import iob.logic.exceptions.activity.UnknownActivityTypeException;
import iob.logic.pagedservices.PagedActivitiesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActivitiesServiceJpa implements PagedActivitiesService {
    //<editor-fold desc="Class variables">
    // Get all the beans that implements `InvokableActivity` and their names.
    // That way, we can return to the user the names of the activities they can invoke.
    // Source: https://stackoverflow.com/a/59440355/9977758
    // This solution is good for our needs because we don't register new beans at runtime.
    private final Map<String, InvokableActivity> possibleActivities;
    private final ActivityConverter activityConverter;
    private final ActivitiesDao activitiesDao;
    private final UserPermissionsHandler userPermissionsHandler;
    private String domainName;
    //</editor-fold>

    @Autowired
    public ActivitiesServiceJpa(ActivityConverter activityConverter, ActivitiesDao activitiesDao,
                                UserPermissionsHandler userPermissionsHandler, Map<String, InvokableActivity> possibleActivities) {
        this.activityConverter = activityConverter;
        this.activitiesDao = activitiesDao;
        this.userPermissionsHandler = userPermissionsHandler;
        this.possibleActivities = possibleActivities;
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

        log.trace("Converting results to boundaries");
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

        validateActivity(activity);

        // Exit early if the there is no activity for the specified type.
        if (!possibleActivities.containsKey(activity.getType())) {
            log.error("Tried to invoke activity of unknown type: {}", activity.getType());
            throw new UnknownActivityTypeException(activity.getType(), possibleActivities.keySet());
        }

        saveEntity(activity);
        return possibleActivities.get(activity.getType())
                .invoke(activity);
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
        log.trace("Validating activity");
        if (StringUtils.isBlank(activity.getType())) {
            log.error("Activity's type is empty");
            throw new InvalidInputException("type", activity.getType());
        }
        if (activity.getInvokedBy() == null) {
            log.error("Activity's invoking user is not specified");
            throw new InvalidInputException("invoked by", null);
        }
        if (activity.getInstance() == null) {
            log.error("Activity's related instance is not specified");
            throw new InvalidInputException("instance", null);
        }
    }

    private void saveEntity(ActivityBoundary activity) {
        ActivityEntity entityToStore = activityConverter.toEntity(activity);
        entityToStore.setCreatedTimestamp(new Date());
        entityToStore.setDomain(domainName);
        log.trace("Converted to an entity");

        entityToStore = activitiesDao.save(entityToStore);
        log.trace("Activity was saved in DB with id: {}", entityToStore.getId());
    }

    @Value("${spring.application.name:dummy}")
    private void setDomainName(String domainName) {
        this.domainName = domainName;
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
}