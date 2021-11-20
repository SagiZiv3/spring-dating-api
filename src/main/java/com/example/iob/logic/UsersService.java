package com.example.iob.logic;

import com.example.iob.boundaries.UserBoundary;

import java.util.List;

public interface UsersService {
    UserBoundary createUser(UserBoundary user);

    UserBoundary login(String userDomain, String userEmail);

    UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update);

    List<UserBoundary> getAllUsers(String adminDomain, String adminEmail);

    void deleteAllUsers(String adminDomain, String adminEmail);
}