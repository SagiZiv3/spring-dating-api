package com.example.demo.boundaries.converters;

import com.example.demo.boundaries.NewUserBoundary;
import com.example.demo.boundaries.UserBoundary;
import com.example.demo.boundaries.UserId;
import com.example.demo.boundaries.UserRole;
import com.example.demo.data.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    @Value("${spring.application.name:dummy}")
    private String applicationDomainName;
    private final IdsConverter idsConverter;

    @Autowired
    public UserConverter(IdsConverter idsConverter) {
        this.idsConverter = idsConverter;
    }

    public UserBoundary toUserBoundary(UserEntity userEntity) {
        UserBoundary boundary = new UserBoundary();
        boundary.setUserId(idsConverter.toUserIdBoundary(userEntity.getId()));
        boundary.setAvatar(userEntity.getAvatar());
        boundary.setRole(toUserRoleBoundary(userEntity.getRole()));
        boundary.setUsername(userEntity.getUsername());
        return boundary;
    }

    public UserBoundary toUserBoundary(NewUserBoundary newUser) {
        UserBoundary boundary = new UserBoundary();
        boundary.setUserId(new UserId(applicationDomainName, newUser.getEmail())); // Create the user with the app's domain
        boundary.setUsername(newUser.getUsername());
        boundary.setRole(newUser.getRole());
        boundary.setAvatar(newUser.getAvatar());
        return boundary;
    }

    public UserEntity toUserEntity(UserBoundary userBoundary) {
        UserEntity entity = new UserEntity();
        entity.setAvatar(userBoundary.getAvatar());
        entity.setUsername(userBoundary.getUsername());
        entity.setId(idsConverter.toUserIdEntity(userBoundary.getUserId()));
        entity.setRole(toUserRoleEntity(userBoundary.getRole()));
        return entity;
    }

    public UserRole toUserRoleBoundary(String userRole) {
        return userRole == null ? null : UserRole.valueOf(userRole);
    }

    private String toUserRoleEntity(UserRole userRole) {
        return userRole == null ? null : userRole.name();
    }

}