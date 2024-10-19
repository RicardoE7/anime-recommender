package com.watchmoreanime.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.watchmoreanime.domain.User;
import com.watchmoreanime.service.UserService;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // This will render the login.html page
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        User authenticatedUser = userService.authenticate(username, password);

        if (authenticatedUser != null) {
            // Return a JSON response with user details
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", authenticatedUser.getId());
            userData.put("username", authenticatedUser.getUsername());
            return ResponseEntity.ok(userData); // Return user data with ID and username
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid username or password."));
        }
    }
}
