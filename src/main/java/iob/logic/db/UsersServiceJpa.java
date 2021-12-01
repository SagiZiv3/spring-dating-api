package iob.logic.db;

import iob.boundaries.UserBoundary;
import iob.boundaries.converters.UserConverter;
import iob.boundaries.helpers.UserRoleBoundary;
import iob.data.UserEntity;
import iob.data.UserRole;
import iob.logic.UsersService;
import iob.logic.db.Daos.UsersDao;
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
        UserEntity entityToStore = this.userConverter.toEntity(user);
        if (!validateEmail(user.getUserId().getEmail())) {
            throw new RuntimeException("Invalid email " + user.getUserId().getEmail());
        }

        // First make sure the user doesn't exist already.
        if (usersDao.existsById(userConverter.toUserPrimaryKey(entityToStore.getEmail(), entityToStore.getDomain()))) {
            throw new RuntimeException("User already exists with email " + user.getUserId().getEmail() +
                    " in domain " + user.getUserId().getDomain());
        }

        entityToStore = usersDao.save(entityToStore);

        return userConverter.toBoundary(entityToStore);
    }

    @Override
    @Transactional(readOnly = true)
    public UserBoundary login(String userDomain, String userEmail) {
        UserEntity entity = usersDao.findById(userConverter.toUserPrimaryKey(userDomain, userEmail))
                .orElseThrow(() -> new RuntimeException("No user with email " + userEmail + " in domain " + userDomain));

        return userConverter.toBoundary(entity);
    }

    @Override
    @Transactional
    public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
        UserEntity entity = usersDao.findById(userConverter.toUserPrimaryKey(userDomain, userEmail))
                .orElseThrow(() -> new RuntimeException("No user with email " + userEmail + " in domain " + userDomain));

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

    private void checkIfAdmin(String adminDomain, String adminEmail) {
        UserEntity userEntity = usersDao.findById(userConverter.toUserPrimaryKey(adminDomain, adminEmail))
                .orElseThrow(() -> new RuntimeException("No user with email " + adminEmail + " in domain " + adminDomain));
        UserBoundary boundary = userConverter.toBoundary(userEntity);
        if (boundary.getRole() != UserRoleBoundary.ADMIN) {
            throw new RuntimeException("The user with email " + adminEmail + " in domain " + adminDomain + " is not an Admin");
        }
    }

    private boolean validateEmail(String email) {
        // Source: https://www.baeldung.com/java-email-validation-regex#:~:text=The%20simplest%20regular%20expression%20to,otherwise%2C%20the%20result%20is%20false.
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
                + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(regexPattern);
    }
}