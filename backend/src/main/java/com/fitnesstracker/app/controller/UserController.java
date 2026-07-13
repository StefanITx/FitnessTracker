package com.fitnesstracker.app.controller;

import com.fitnesstracker.app.model.User;
import com.fitnesstracker.app.repository.UserRepository;
import com.fitnesstracker.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/api/users")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @PostMapping("api/users")
    public User createUser(@Valid @RequestBody User user){
        return userService.createUser(user);
    }
}
