package iob.logic.mockups;

import iob.boundaries.UserBoundary;
import iob.boundaries.converters.UserConverter;
import iob.data.UserEntity;
import iob.logic.UsersService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Service
public class UsersServiceMockup implements UsersService {
    private final UserConverter userConverter;
    private Map<String, UserEntity> storage;

    @Autowired
    public UsersServiceMockup(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    @PostConstruct
    private void init() {
        this.storage = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public UserBoundary createUser(UserBoundary user) {
        return null;
//        UserEntity entityToStore = this.userConverter.toUserEntity(user);
//        // First make sure the user doesn't exist already.
//        if (storage.containsKey(entityToStore.getId())) {
//            throw new RuntimeException("User already exists with email " + user.getUserId().getEmail() +
//                    " in domain " + user.getUserId().getDomain());
//        }
//        // To make sure that the user is not saved twice (e.g. 2 threads creating the same user)
//        this.storage.putIfAbsent(entityToStore.getId(), entityToStore);
//
//        return userConverter.toUserBoundary(entityToStore);
    }

    @Override
    public UserBoundary login(String userDomain, String userEmail) {
        return null;
//        return userConverter.toUserBoundary(getUserFromStorage(userDomain, userEmail));
    }

    @Override
    public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
        return null;
//        UserEntity userEntity = getUserFromStorage(userDomain, userEmail);
//        boolean isDirty = false;
//
//        if (update.getRole() != null) {
//            userEntity.setRole(UserRole.valueOf(update.getRole().name()));
//            isDirty = true;
//        }
//        if (update.getUsername() != null) {
//            userEntity.setUsername(update.getUsername());
//            isDirty = true;
//        }
//        if (update.getAvatar() != null) {
//            userEntity.setAvatar(update.getAvatar());
//            isDirty = true;
//        }
//
//        if (isDirty) {
//            storage.put(userEntity.getId(), userEntity);
//            userEntity = getUserFromStorage(userDomain, userEmail);
//        }
//        return userConverter.toUserBoundary(userEntity);
    }

    @Override
    public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
        return null;
//        checkIfAdmin(adminDomain, adminEmail);
//        return storage
//                .values()
//                .stream()
//                .map(this.userConverter::toUserBoundary)
//                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllUsers(String adminDomain, String adminEmail) {
//        checkIfAdmin(adminDomain, adminEmail);
//        this.storage.clear();
    }

    private void checkIfAdmin(String adminDomain, String adminEmail) {
//        UserEntity userEntity = getUserFromStorage(adminDomain, adminEmail);
//        UserBoundary boundary = userConverter.toUserBoundary(userEntity);
//        if (boundary.getRole() != UserRoleBoundary.ADMIN) {
//            throw new RuntimeException("The user with email " + adminEmail + " in domain " + adminDomain + " is not an Admin");
//        }
    }

    private UserEntity getUserFromStorage(String domain, String email) {
        return null;
//        String key = idsConverter.toUserIdEntity(new UserIdBoundary(domain, email));
//        UserEntity userEntity = storage.get(key);
//        if (userEntity == null) {
//            throw new RuntimeException("Couldn't find a user with email " + email + " in domain " + domain);
//        }
//        return userEntity;
    }
}