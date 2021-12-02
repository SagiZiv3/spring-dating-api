package iob.logic.db;

import iob.boundaries.UserBoundary;
import iob.boundaries.converters.UserConverter;
import iob.data.UserEntity;
import iob.data.UserRole;
import iob.logic.UsersService;
import iob.logic.db.dao.UsersDao;
import iob.logic.exceptions.InvalidInputException;
import iob.logic.exceptions.user.UserExistsException;
import iob.logic.exceptions.user.UserNotFoundException;
import iob.logic.exceptions.user.UserPermissionException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UsersServiceJpa implements UsersService {
    private final UserConverter userConverter;
    private final UsersDao usersDao;

    @Autowired
    public UsersServiceJpa(UserConverter userConverter, UsersDao usersDao) {
        this.userConverter = userConverter;
        this.usersDao = usersDao;
    }

    @Override
    @Transactional
    public UserBoundary createUser(UserBoundary user) {
        validateUser(user);
        UserEntity entityToStore = this.userConverter.toEntity(user);

        // Make sure the user doesn't already exist.
        if (usersDao.existsById(userConverter.toUserPrimaryKey(entityToStore.getEmail(), entityToStore.getDomain()))) {
            throw new UserExistsException(entityToStore.getDomain(), entityToStore.getEmail());
        }

        entityToStore = usersDao.save(entityToStore);

        return userConverter.toBoundary(entityToStore);
    }

    @Override
    @Transactional(readOnly = true)
    public UserBoundary login(String userDomain, String userEmail) {
        UserEntity entity = findUserInStorage(userDomain, userEmail);

        return userConverter.toBoundary(entity);
    }

    @Override
    @Transactional
    public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
        UserEntity entity = findUserInStorage(userDomain, userEmail);

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
        return userConverter.toBoundary(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
        checkIfAdmin(adminDomain, adminEmail);
        return StreamSupport
                .stream(this.usersDao
                        .findAll()
                        .spliterator(), false)
                .map(this.userConverter::toBoundary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllUsers(String adminDomain, String adminEmail) {
        checkIfAdmin(adminDomain, adminEmail);
        usersDao.deleteAll();
    }

    /**
     * Checks if all the fields in the given user are valid (non-empty and not null).<br/>
     * If one of the fields is invalid, an {@link InvalidInputException} would be thrown.
     *
     * @param userBoundary - The user boundary to validate.
     */
    private void validateUser(@NonNull UserBoundary userBoundary) {
        // Make sure the given email is valid
        if (!validateEmail(userBoundary.getUserId().getEmail())) {
            throw new InvalidInputException("email", userBoundary.getUserId().getEmail());
        }
        if (userBoundary.getUsername() == null || userBoundary.getUsername().isEmpty()) {
            throw new InvalidInputException("username", userBoundary.getUsername());
        }
        if (userBoundary.getAvatar() == null || userBoundary.getAvatar().isEmpty()) {
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
    private boolean validateEmail(@NonNull String email) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
                + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(regexPattern);
    }

    /**
     * Finds the user in the storage and checks if his role is defined as {@link UserRole#ADMIN}.<br/>
     * If the user is not an admin, a {@link UserPermissionException} would be thrown.
     *
     * @param domain - The domain in which the user exists.
     * @param email  - The user's email address.
     */
    private void checkIfAdmin(String domain, String email) {
        UserEntity userEntity = findUserInStorage(domain, email);
        if (userEntity.getRole() != UserRole.ADMIN) {
            throw new UserPermissionException(userEntity.getRole().name(), UserRole.ADMIN.name(),
                    userEntity.getEmail(), userEntity.getDomain());
        }
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
}