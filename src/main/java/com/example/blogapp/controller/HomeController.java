package com.example.blogapp.controller;

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

@Controller
public class HomeController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Blog> blogs = blogService.findAll();
        model.addAttribute("blogs", blogs);
        if (userDetails != null) {
            User currentUser = userService.findByUsername(userDetails.getUsername()).orElse(null);
            model.addAttribute("currentUser", currentUser);
        }
        return "home";
    }

    @GetMapping("/following")
    public String following(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User currentUser = userService.findByUsername(userDetails.getUsername()).orElse(null);
            if (currentUser != null) {
                List<Blog> blogs = blogService.findByUsers(List.copyOf(currentUser.getFollowing()));
                model.addAttribute("blogs", blogs);
                model.addAttribute("currentUser", currentUser);
            }
        }
        return "following";
    }
}
