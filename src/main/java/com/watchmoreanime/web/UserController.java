package com.watchmoreanime.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.watchmoreanime.domain.User;
import com.watchmoreanime.service.UserService;

@Controller
public class UserController {
	@Autowired
	private UserService userService;

    @GetMapping("/register")
    public String getRegister(Model model) {
        User user = new User();  // Create a new User object
        model.addAttribute("user", user);  // Add the User object to the model
        
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
     
        userService.save(user);
        
        return "redirect:/recommended"; // Redirect to the registration page or another page
    }

    @GetMapping("/recommended")
    public String getRecommended(){
        return "recommended";
    }

}
