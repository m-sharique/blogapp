package com.example.blogapp.controller;

import com.example.blogapp.model.User;
import com.example.blogapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.logging.Logger;

@Controller
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(User user) {
        userService.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/follow")
    public String follow(@RequestParam Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Attempting to follow user with ID: " + userId);

        if (userDetails == null) {
            logger.warning("UserDetails is null");
            return "redirect:/login";
        }

        Optional<User> optionalCurrentUser = userService.findByUsername(userDetails.getUsername());
        Optional<User> optionalUserToFollow = userService.findById(userId);

        if (optionalCurrentUser.isPresent() && optionalUserToFollow.isPresent()) {
            User currentUser = optionalCurrentUser.get();
            User userToFollow = optionalUserToFollow.get();
            currentUser.getFollowing().add(userToFollow);
            userService.save(currentUser);
            logger.info("User with ID: " + currentUser.getId() + " is now following user with ID: " + userId);
        } else {
            logger.warning("User not found");
        }

        return "redirect:/";
    }

    @PostMapping("/unfollow")
    public String unfollow(@RequestParam Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Attempting to unfollow user with ID: " + userId);

        if (userDetails == null) {
            logger.warning("UserDetails is null");
            return "redirect:/login";
        }

        Optional<User> optionalCurrentUser = userService.findByUsername(userDetails.getUsername());
        Optional<User> optionalUserToUnfollow = userService.findById(userId);

        if (optionalCurrentUser.isPresent() && optionalUserToUnfollow.isPresent()) {
            User currentUser = optionalCurrentUser.get();
            User userToUnfollow = optionalUserToUnfollow.get();
            currentUser.getFollowing().remove(userToUnfollow);
            userService.save(currentUser);
            logger.info("User with ID: " + currentUser.getId() + " has unfollowed user with ID: " + userId);
        } else {
            logger.warning("User not found");
        }

        return "redirect:/";
    }
}
