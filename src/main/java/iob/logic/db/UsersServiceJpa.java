package iob.logic.db;

import iob.boundaries.UserBoundary;
import iob.boundaries.converters.IdsConverter;
import iob.boundaries.converters.UserConverter;
import iob.boundaries.helpers.UserId;
import iob.boundaries.helpers.UserRole;
import iob.data.UserEntity;
import iob.logic.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UsersServiceJpa implements UsersService {
    private final UserConverter userConverter;
    private final IdsConverter idsConverter;
    private final UsersDao usersDao;

    @Autowired
    public UsersServiceJpa(UserConverter userConverter, IdsConverter idsConverter, UsersDao usersDao) {
        this.userConverter = userConverter;
        this.idsConverter = idsConverter;
        this.usersDao = usersDao;
    }

    @Override
    @Transactional
    public UserBoundary createUser(UserBoundary user) {
        UserEntity entityToStore = this.userConverter.toUserEntity(user);
        if (!validateEmail(user.getUserId().getEmail())) {
            throw new RuntimeException("Invalid email " + user.getUserId().getEmail());
        }

        // First make sure the user doesn't exist already.
        if (usersDao.existsById(entityToStore.getId())) {
            throw new RuntimeException("User already exists with email " + user.getUserId().getEmail() +
                    " in domain " + user.getUserId().getDomain());
        }

        entityToStore = usersDao.save(entityToStore);

        return userConverter.toUserBoundary(entityToStore);
    }

    @Override
    @Transactional(readOnly = true)
    public UserBoundary login(String userDomain, String userEmail) {
        UserEntity entity = usersDao.findById(idsConverter.toUserIdEntity(new UserId(userDomain, userEmail)))
                .orElseThrow(() -> new RuntimeException("No user with email " + userEmail + " in domain " + userDomain));

        return userConverter.toUserBoundary(entity);
    }

    @Override
    @Transactional
    public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
        UserEntity entity = usersDao.findById(idsConverter.toUserIdEntity(new UserId(userDomain, userEmail)))
                .orElseThrow(() -> new RuntimeException("No user with email " + userEmail + " in domain " + userDomain));

        if (update.getRole() != null) {
            entity.setRole(update.getRole().name());
        }
        if (update.getUsername() != null) {
            entity.setUsername(update.getUsername());
        }
        if (update.getAvatar() != null) {
            entity.setAvatar(update.getAvatar());
        }

        entity = usersDao.save(entity);
        return userConverter.toUserBoundary(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
        checkIfAdmin(adminDomain, adminEmail);
        return StreamSupport
                .stream(this.usersDao
                        .findAll()
                        .spliterator(), false)
                .map(this.userConverter::toUserBoundary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllUsers(String adminDomain, String adminEmail) {
        checkIfAdmin(adminDomain, adminEmail);
        usersDao.deleteAll();
    }

    private void checkIfAdmin(String adminDomain, String adminEmail) {
        UserEntity userEntity = usersDao.findById(idsConverter.toUserIdEntity(new UserId(adminDomain, adminEmail)))
                .orElseThrow(() -> new RuntimeException("No user with email " + adminEmail + " in domain " + adminDomain));
        UserBoundary boundary = userConverter.toUserBoundary(userEntity);
        if (boundary.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("The user with email " + adminEmail + " in domain " + adminDomain + " is not an Admin");
        }
    }

    private boolean validateEmail(String email) {
        // Source: https://www.baeldung.com/java-email-validation-regex#:~:text=The%20simplest%20regular%20expression%20to,otherwise%2C%20the%20result%20is%20false.
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
                + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
    }
}