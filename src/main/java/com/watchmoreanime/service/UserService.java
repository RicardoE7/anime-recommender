package com.watchmoreanime.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.watchmoreanime.domain.User;
import com.watchmoreanime.dto.RegistrationDTO;
import com.watchmoreanime.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void registerUser(RegistrationDTO registrationDTO) {
        // Check if username already exists
        if (userRepository.findByUsername(registrationDTO.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        // Create a new user
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(registrationDTO.getPassword()); // No encryption

        // Save user to the database
        userRepository.save(user);
    }
    
    public void save(User user) {
    	userRepository.save(user);
    }

    public User authenticate(String username, String password) {
        // Find the user by username
        User user = userRepository.findByUsername(username);

        // If the user exists and the password matches, return the user
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        // Otherwise, return null indicating authentication failure
        return null;
    }
}
