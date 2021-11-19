package com.example.demo.logic;

import com.example.demo.boundaries.UserBoundary;

import java.util.List;

public interface UsersService {
    UserBoundary createUser(UserBoundary user);

    UserBoundary login(String userDomain, String userEmail);

    UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update);

    List<UserBoundary> getAllUsers(String adminDomain, String adminEmail);

    void deleteAllUsers(String adminDomain, String adminEmail);
}