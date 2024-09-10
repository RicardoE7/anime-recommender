package com.watchmoreanime.web;

import com.watchmoreanime.domain.User;
import com.watchmoreanime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // This will render the login.html page
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            Model model) {
        // Authenticate user
        User authenticatedUser = userService.authenticate(username, password);

        // If authentication is successful
        if (authenticatedUser != null) {
            // Store user session, redirect to recommended page (or wherever you want)
            // For now, just redirect to a successful login page or a recommendation page
            return "redirect:/recommended"; // Redirect to your recommendation page
        } else {
            // Authentication failed, return to login page with an error message
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }
}
