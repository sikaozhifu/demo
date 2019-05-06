package com.example.demo.service;

import com.example.demo.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUser();

    Integer delete(Integer id);

    User getUserById(Integer id);
}
