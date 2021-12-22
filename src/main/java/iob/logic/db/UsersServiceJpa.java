package iob.logic.db;

import iob.boundaries.UserBoundary;
import iob.boundaries.converters.UserConverter;
import iob.data.UserEntity;
import iob.data.UserRole;
import iob.logic.annotations.ParameterType;
import iob.logic.annotations.RoleParameter;
import iob.logic.annotations.RoleRestricted;
import iob.logic.annotations.UserRoleParameter;
import iob.logic.db.dao.UsersDao;
import iob.logic.exceptions.InvalidInputException;
import iob.logic.exceptions.user.UserExistsException;
import iob.logic.exceptions.user.UserNotFoundException;
import iob.logic.pagedservices.PagedUsersService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsersServiceJpa implements PagedUsersService {
    //<editor-fold desc="Class variables">
    private final UserConverter userConverter;
    private final UsersDao usersDao;
    //</editor-fold>

    @Autowired
    public UsersServiceJpa(UserConverter userConverter, UsersDao usersDao) {
        this.userConverter = userConverter;
        this.usersDao = usersDao;
    }

    //<editor-fold desc="Get methods">
    @Override
    @Transactional(readOnly = true)
    public UserBoundary login(String userDomain, String userEmail) {
        log.info("Searching user in DB");
        UserEntity entity = findUserInStorage(userDomain, userEmail);
        log.info("User from DB: {}", entity);
        return userConverter.toBoundary(entity);
    }

    @Override
    @Transactional(readOnly = true)
    @RoleRestricted(permittedRoles = UserRoleParameter.ADMIN)
    public List<UserBoundary> getAllUsers(@RoleParameter(parameterType = ParameterType.DOMAIN) String adminDomain,
                                          @RoleParameter(parameterType = ParameterType.EMAIL) String adminEmail, int page, int size) {
        log.info("Getting {} users from page {}", size, page);
        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction, "email", "username");

        Page<UserEntity> resultPage = this.usersDao
                .findAll(pageable);

        log.info("Converting results to boundaries");
        return resultPage
                .stream()
                .map(this.userConverter::toBoundary)
                .collect(Collectors.toList());
    }

    //</editor-fold>

    //<editor-fold desc="Modification methods (create/update/delete)">
    @Override
    @Transactional
    public UserBoundary createUser(UserBoundary user) {
        log.info("Validating user");
        validateUser(user);
        UserEntity entityToStore = this.userConverter.toEntity(user);
        log.info("Converted to entity: {}", entityToStore);

        // Make sure the user doesn't already exist.
        if (usersDao.existsById(userConverter.toUserPrimaryKey(entityToStore.getEmail(), entityToStore.getDomain()))) {
            log.error("User with email {} already exists in domain {}", entityToStore.getEmail(), entityToStore.getDomain());
            throw new UserExistsException(entityToStore.getDomain(), entityToStore.getEmail());
        }

        entityToStore = usersDao.save(entityToStore);
        log.info("User was saved in DB: {}", entityToStore);

        return userConverter.toBoundary(entityToStore);
    }

    @Override
    @Transactional
    public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
        log.info("Searching user in DB");
        UserEntity entity = findUserInStorage(userDomain, userEmail);

        log.info("Updating user's data");
        if (update.getRole() != null) {
            entity.setRole(UserRole.valueOf(update.getRole().name()));
        }
        if (update.getUsername() != null) {
            entity.setUsername(update.getUsername());
        }
        if (update.getAvatar() != null) {
            entity.setAvatar(update.getAvatar());
        }

        entity = usersDao.save(entity);
        log.info("Modified user was saved in DB: {}", entity);
        return userConverter.toBoundary(entity);
    }

    @Override
    @Transactional
    @RoleRestricted(permittedRoles = UserRoleParameter.ADMIN)
    public void deleteAllUsers(@RoleParameter(parameterType = ParameterType.DOMAIN) String adminDomain,
                               @RoleParameter(parameterType = ParameterType.EMAIL) String adminEmail) {
        log.info("Deleting all users");
        usersDao.deleteAll();
    }
    //</editor-fold>

    //<editor-fold desc="Helper methods">

    /**
     * Checks if all the fields in the given user are valid (non-empty and not null).<br/>
     * If one of the fields is invalid, an {@link InvalidInputException} would be thrown.
     *
     * @param userBoundary - The user boundary to validate.
     */
    private void validateUser(@NonNull UserBoundary userBoundary) {
        // Make sure the given email is valid
        if (StringUtils.isBlank(userBoundary.getUserId().getEmail()) || !validateEmail(userBoundary.getUserId().getEmail())) {
            throw new InvalidInputException("email", userBoundary.getUserId().getEmail());
        }
        if (StringUtils.isBlank(userBoundary.getUsername())) {
            throw new InvalidInputException("username", userBoundary.getUsername());
        }
        if (StringUtils.isBlank(userBoundary.getAvatar())) {
            throw new InvalidInputException("avatar", userBoundary.getAvatar());
        }
    }

    /**
     * Checks if the given email address is valid
     *
     * @param email - The email address to validate.
     * @return `true` if the email is valid, `false` otherwise.
     * @see <a href="https://www.baeldung.com/java-email-validation-regex#:~:text=The%20simplest%20regular%20expression%20to,otherwise%2C%20the%20result%20is%20false">Source</a>
     */
    private boolean validateEmail(String email) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
                + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(regexPattern);
    }

    /**
     * Searches and retrieves the user with the given info from the storage.<br/>
     * If the user doesn't exist, a {@link UserNotFoundException} would be thrown.
     *
     * @param domain - The domain in which the user exists.
     * @param email  - The user's email address.
     * @return A {@link UserEntity} for the given domain and email address.
     */
    private UserEntity findUserInStorage(String domain, String email) {
        return usersDao.findById(userConverter.toUserPrimaryKey(email, domain))
                .orElseThrow(() -> new UserNotFoundException(domain, email));
    }
    //</editor-fold>

    //<editor-fold desc="Deprecated methods">
    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
        log.error("Called deprecated method");
        throw new RuntimeException("Unimplemented deprecated operation");
    }
    //</editor-fold>
}