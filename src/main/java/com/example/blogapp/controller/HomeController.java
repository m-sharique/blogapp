package com.example.blogapp.controller;

// Import necessary classes and packages
import com.example.blogapp.model.Blog;
import com.example.blogapp.model.User;
import com.example.blogapp.service.BlogService;
import com.example.blogapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;

// Define the class as a Controller in the Spring MVC framework
@Controller
public class HomeController {

    // Autowire the BlogService and UserService
    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    // Define a GET mapping for "/" URL
    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Fetch all blogs
        List<Blog> blogs = blogService.findAll();
        // Add the blogs to the model
        model.addAttribute("blogs", blogs);
        // If the user is authenticated
        if (userDetails != null) {
            // Find the user by username
            User currentUser = userService.findByUsername(userDetails.getUsername()).orElse(null);
            // Get the users that the current user is following
            Set<User> following = currentUser.getFollowing();
            // Add the current user and the users they are following to the model
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("following", following);
        }
        // Return the name of the view to be rendered
        return "home";
    }

    // Define a GET mapping for "/following" URL
    @GetMapping("/following")
    public String following(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // If the user is authenticated
        if (userDetails != null) {
            // Find the user by username
            User currentUser = userService.findByUsername(userDetails.getUsername()).orElse(null);
            // If the user is found
            if (currentUser != null) {
                // Fetch the blogs of the users that the current user is following
                List<Blog> blogs = blogService.findByUsers(List.copyOf(currentUser.getFollowing()));
                // Add the blogs and the current user to the model
                model.addAttribute("blogs", blogs);
                model.addAttribute("currentUser", currentUser);
            }
        }
        // Return the name of the view to be rendered
        return "following";
    }
}