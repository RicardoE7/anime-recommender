package com.watchmoreanime.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.watchmoreanime.service.UserService;

@RestController
@RequestMapping("/update-profile")
public class ProfileController {
	@Autowired UserService userService;
    @PostMapping
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest request) {
        // Validate the current password
        boolean isValid = userService.validatePassword(request.getUsername(), request.getCurrentPassword());
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid current password."));
        }
        
        if (userService.isUsernameTaken(request.getNewUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username is already taken. Please choose another one.");
        }

        // Update user details
        userService.updateUserProfile(
            request.getUsername(),
            request.getNewUsername(),
            request.getEmail(),
            request.getNewPassword()
        );

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully."));
    }
}

class ProfileUpdateRequest {
    private String username;
    private String email;
    private String newUsername;
    private String newPassword;
    private String currentPassword;
    // Getters and setters
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getNewUsername() {
		return newUsername;
	}
	public void setNewUsername(String newUsername) {
		this.newUsername = newUsername;
	}
    
}

