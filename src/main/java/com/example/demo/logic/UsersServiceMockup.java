package com.example.demo.logic;

import com.example.demo.boundaries.UserBoundary;
import com.example.demo.boundaries.converters.UserConverter;
import com.example.demo.data.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class UsersServiceMockup implements UsersService {
    private final UserConverter userConverter;
    private Map<Long, UserEntity> storage;
    private AtomicLong counter;

    @Autowired
    public UsersServiceMockup(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    @PostConstruct
    private void init() {
        this.storage = Collections.synchronizedMap(new HashMap<>());
        this.counter = new AtomicLong(1L);
    }

    @Override
    public UserBoundary createUser(UserBoundary user) {
        UserEntity entityToStore = this.userConverter.toUserEntity(user);

        counter.incrementAndGet();
//        entityToStore.setId(this.counter.getAndIncrement());
//
//        this.storage.put(entityToStore.getId(), entityToStore);

        return this.userConverter.toUserBoundary(entityToStore);
    }

    @Override
    public UserBoundary login(String userDomain, String userEmail) {
        return null;
    }

    @Override
    public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
        return null;
    }

    @Override
    public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
        return this.storage
                .values()
                .stream()
                .map(this.userConverter::toUserBoundary)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllUsers(String adminDomain, String adminEmail) {
        this.storage.clear();
    }
}