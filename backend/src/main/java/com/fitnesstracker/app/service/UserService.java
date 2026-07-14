package com.fitnesstracker.app.service;

import com.fitnesstracker.app.exception.EmailAlreadyTakenException;
import com.fitnesstracker.app.exception.InvalidCredentialsException;
import com.fitnesstracker.app.model.User;
import com.fitnesstracker.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyTakenException(user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User checkUserLoggedIn(String email,String password){
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isPresent()){
            if(passwordEncoder.matches(password,user.get().getPassword())){
                return  user.get();
            }
            throw new InvalidCredentialsException();
        }
        throw new InvalidCredentialsException();
    }
}
