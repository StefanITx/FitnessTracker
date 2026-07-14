package com.fitnesstracker.app.controller;

import com.fitnesstracker.app.dto.AuthResponse;
import com.fitnesstracker.app.dto.LoginRequest;
import com.fitnesstracker.app.dto.RegisterRequest;
import com.fitnesstracker.app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;


    @PostMapping("/register")
    public AuthResponse registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public AuthResponse loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

}
