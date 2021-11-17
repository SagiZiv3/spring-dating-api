package com.example.demo.boundaries.converters;

import com.example.demo.boundaries.UserBoundary;
import com.example.demo.boundaries.UserId;
import com.example.demo.boundaries.UserRole;
import com.example.demo.data.UserEntity;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public UserBoundary toUserBoundary(UserEntity userEntity) {
        UserBoundary boundary = new UserBoundary();
        boundary.setUserId(toUserIdBoundary(userEntity.getId()));
        boundary.setAvatar(userEntity.getAvatar());
        boundary.setRole(toUserRoleBoundary(userEntity.getRole()));
        boundary.setUsername(userEntity.getUsername());
        return boundary;
    }

    public UserId toUserIdBoundary(@NonNull String userIdString) {
        String[] values = userIdString.split(";");
        return new UserId(values[0], values[1]);
    }

    public UserRole toUserRoleBoundary(String userRole) {
        if (userRole == null)
            return null;

        return UserRole.valueOf(userRole);
    }

    public UserEntity toUserEntity(UserBoundary userBoundary) {
        UserEntity entity = new UserEntity();
        entity.setAvatar(userBoundary.getAvatar());
        entity.setUsername(userBoundary.getUsername());
        entity.setId(toUserIdEntity(userBoundary.getUserId()));
        entity.setRole(toUserRoleEntity(userBoundary.getRole()));
        return entity;
    }

    public String toUserIdEntity(UserId userId) {
        return String.format("%s;%s", userId.getDomain(), userId.getEmail());
    }

    private String toUserRoleEntity(UserRole userRole) {
        if (userRole == null)
            return null;

        return userRole.name();
    }
}