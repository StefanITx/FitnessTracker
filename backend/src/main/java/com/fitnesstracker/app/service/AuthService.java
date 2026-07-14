package com.fitnesstracker.app.service;

import com.fitnesstracker.app.dto.AuthResponse;
import com.fitnesstracker.app.dto.LoginRequest;
import com.fitnesstracker.app.dto.RegisterRequest;
import com.fitnesstracker.app.model.User;
import com.fitnesstracker.app.repository.UserRepository;
import com.fitnesstracker.app.security.JwtUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    public AuthResponse register(@NonNull RegisterRequest request){
        User user=new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        User createdUser=userService.createUser(user);
        return new AuthResponse(jwtUtils.generateToken(createdUser.getEmail()));
    }

    public AuthResponse login(LoginRequest request){
        User user = userService.checkUserLoggedIn(request.getEmail(), request.getPassword());
        return new AuthResponse(jwtUtils.generateToken(user.getEmail()));
    }
}
