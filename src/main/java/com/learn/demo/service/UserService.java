package com.learn.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.learn.demo.model.User;

@Service
public interface  UserService {

    User saveUser(User user);
    List<User> getAllUsers();
    User getUserById(Long userId);
    User updateUser(Long userId, User newUser);
    void deleteUser(Long userId);

    List<User> searchUsers(String username, String role);

}


//there we just created a function,we declared this functions on serviceimpl(we use it),then access by contoller