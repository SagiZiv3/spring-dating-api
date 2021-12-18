package iob.boundaries.converters;

import iob.boundaries.NewUserBoundary;
import iob.boundaries.UserBoundary;
import iob.boundaries.helpers.UserIdBoundary;
import iob.boundaries.helpers.UserRoleBoundary;
import iob.data.UserEntity;
import iob.data.UserRole;
import iob.data.primarykeys.UserPrimaryKey;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    private String applicationDomainName;

    public UserBoundary toBoundary(@NonNull UserEntity entity) {
        UserBoundary boundary = new UserBoundary();
        boundary.setUserId(toUserIdBoundary(entity.getDomain(), entity.getEmail()));
        boundary.setUsername(entity.getUsername());
        boundary.setAvatar(entity.getAvatar());
        boundary.setRole(toUserRoleBoundary(entity.getRole()));
        return boundary;
    }

    public UserIdBoundary toUserIdBoundary(String domain, String email) {
        return new UserIdBoundary(domain, email);
    }

    private UserRoleBoundary toUserRoleBoundary(UserRole userRole) {
        if (userRole == null) return null;
        return UserRoleBoundary.values()[userRole.ordinal()];
    }

    public UserBoundary toBoundary(NewUserBoundary newUser) {
        UserBoundary boundary = new UserBoundary();
        // Create the user with the app's domain
        boundary.setUserId(new UserIdBoundary(applicationDomainName, newUser.getEmail()));
        boundary.setUsername(newUser.getUsername());
        boundary.setRole(newUser.getRole());
        boundary.setAvatar(newUser.getAvatar());
        System.out.println("UserConverter - toBoundary - " + boundary.getUserId().getDomain());
        return boundary;
    }

    public UserEntity toEntity(UserBoundary userBoundary) {
        UserEntity entity = new UserEntity();
        entity.setAvatar(userBoundary.getAvatar());
        entity.setUsername(userBoundary.getUsername());
        entity.setRole(toUserRoleEntity(userBoundary.getRole()));
        if (userBoundary.getUserId() != null) {
            UserPrimaryKey userKey = toUserPrimaryKey(userBoundary.getUserId());
            entity.setDomain(userKey.getDomain());
            entity.setEmail(userKey.getEmail());
        }
        return entity;
    }

    private UserRole toUserRoleEntity(UserRoleBoundary userRole) {
        if (userRole == null) return null;
        return UserRole.values()[userRole.ordinal()];
    }

    public UserPrimaryKey toUserPrimaryKey(UserIdBoundary userId) {
        return new UserPrimaryKey(userId.getDomain(), userId.getEmail());
    }

    public UserPrimaryKey toUserPrimaryKey(String email, String domain) {
        return new UserPrimaryKey(domain, email);
    }

    @Value("${spring.application.name:dummy}")
    public void setDomainName(String domainName) {
        this.applicationDomainName = domainName;
    }
}