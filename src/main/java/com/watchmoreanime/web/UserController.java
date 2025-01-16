package com.watchmoreanime.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.domain.User;
import com.watchmoreanime.dto.WatchlistRequest;
import com.watchmoreanime.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController // Changed to RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String getRegister() {
        // Return a simple view or a message
        return "Register page"; // You can update this to return a proper view if needed
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            // Check if username already exists
            if (userService.isUsernameTaken(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Username is already taken. Please choose another one.");
            }

            userService.save(user); // Save the user
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed. Please try again.");
        }
    }

    @GetMapping("/recommended")
    public String getRecommended() {
        return "recommended";
    }
    
    @PostMapping("/watchlist")
    public ResponseEntity<?> addToWatchlist(@RequestBody WatchlistRequest request) {
        boolean success = userService.addAnimeToWatchlist(request.getUserId(), request.getAnimeId(), request.getRating());
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to add to watchlist."));
        }
    }
    
    @GetMapping("/{userId}/watchlist")
    public ResponseEntity<List<Anime>> getWatchList(@PathVariable Long userId) {
    	List<Anime> watchList = userService.getWatchListWithRatings(userId);
        return ResponseEntity.ok(watchList);
    }
    
    @GetMapping("/get-user-data")
    public ResponseEntity<?> getUserData(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("loggedInUser");
        if (user != null) {
            Map<String, String> userData = new HashMap<>();
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            return ResponseEntity.ok(userData);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
    }
}

