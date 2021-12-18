package iob.logic.pagedservices;

import iob.boundaries.UserBoundary;
import iob.logic.UsersService;

import java.util.List;

public interface PagedUsersService extends UsersService {
    List<UserBoundary> getAllUsers(String adminDomain, String adminEmail, int page, int size);
}