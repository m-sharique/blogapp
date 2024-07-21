package com.example.blogapp.controller;

// Import necessary classes and packages
import com.example.blogapp.model.ProfilePicture;
import com.example.blogapp.model.User;
import com.example.blogapp.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Logger;

// Define the class as a Controller in the Spring MVC framework
@Controller
public class UserController {

    // Define a Logger for logging information and warnings
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    // Autowire the UserService and EntityManager
    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager entityManager;

    // Define a GET mapping for "/register" URL
    @GetMapping("/register")
    public String registerForm(Model model) {
        // Add a new User object to the model
        model.addAttribute("user", new User());
        // Return the name of the view to be rendered
        return "register";
    }

    // Define a POST mapping for "/register" URL
    @PostMapping("/register")
    public String register(User user) {
        // Save the user
        userService.save(user);
        // Redirect to the login page
        return "redirect:/login";
    }

    // Define a GET mapping for "/login" URL
    @GetMapping("/login")
    public String login() {
        // Return the name of the view to be rendered
        return "login";
    }

    // Define a POST mapping for "/follow" URL
    @Transactional
    @PostMapping("/follow")
    public String follow(@RequestParam Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        // Log the attempt to follow a user
        logger.info("Attempting to follow user with ID: " + userId);

        // If the user is not authenticated, redirect to the login page
        if (userDetails == null) {
            logger.warning("UserDetails is null");
            return "redirect:/login";
        }

        // Find the current user and the user to follow
        Optional<User> optionalCurrentUser = userService.findByUsername(userDetails.getUsername());
        Optional<User> optionalUserToFollow = userService.findById(userId);

        // If both users are found
        if (optionalCurrentUser.isPresent() && optionalUserToFollow.isPresent()) {
            User currentUser = optionalCurrentUser.get();
            User userToFollow = optionalUserToFollow.get();

            // If the current user is trying to follow themselves, redirect to the home page
            if (currentUser.equals(userToFollow)) {
                logger.warning("User cannot follow themselves");
                return "redirect:/";
            }

            // If the current user is already following the user to follow, redirect to the home page
            if (currentUser.getFollowing().contains(userToFollow)) {
                logger.warning("User is already following the target user");
                return "redirect:/";
            }

            // Add the user to follow to the current user's following list and save both users
            currentUser.getFollowing().add(userToFollow);
            userToFollow.getFollowers().add(currentUser);
            userService.save(currentUser);
            userService.save(userToFollow);

            // Log the successful follow
            logger.info("User with ID: " + currentUser.getId() + " is now following user with ID: " + userToFollow.getId());
        } else {
            // If one or both users are not found, log a warning
            logger.warning("User not found");
        }

        // Redirect to the home page
        return "redirect:/";
    }

    // Define a POST mapping for "/unfollow" URL
    @Transactional
    @PostMapping("/unfollow")
    public String unfollow(@RequestParam Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        // Log the attempt to unfollow a user
        logger.info("Attempting to unfollow user with ID: " + userId);

        // If the user is not authenticated, redirect to the login page
        if (userDetails == null) {
            logger.warning("UserDetails is null");
            return "redirect:/login";
        }

        // Find the current user and the user to unfollow
        Optional<User> optionalCurrentUser = userService.findByUsername(userDetails.getUsername());
        Optional<User> optionalUserToUnfollow = userService.findById(userId);

        // If both users are found
        if (optionalCurrentUser.isPresent() && optionalUserToUnfollow.isPresent()) {
            User currentUser = optionalCurrentUser.get();
            User userToUnfollow = optionalUserToUnfollow.get();

            // If the current user is not following the user to unfollow, redirect to the home page
            if (!currentUser.getFollowing().contains(userToUnfollow)) {
                logger.warning("User is not following the target user");
                return "redirect:/";
            }

            // Remove the user to unfollow from the current user's following list and save both users
            currentUser.getFollowing().remove(userToUnfollow);
            userToUnfollow.getFollowers().remove(currentUser);
            userService.save(currentUser);
            userService.save(userToUnfollow);

            // Log the successful unfollow
            logger.info("User with ID: " + currentUser.getId() + " has unfollowed user with ID: " + userToUnfollow.getId());
        } else {
            // If one or both users are not found, log a warning
            logger.warning("User not found");
        }

        // Redirect to the home page
        return "redirect:/";
    }

    // Define a POST mapping for "/profile/picture" URL
    @PostMapping("/profile/picture")
    public String updateProfilePicture(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails, Model model) throws IOException {
        // If the user is not authenticated, redirect to the login page
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Find the user
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            // If the file size exceeds the limit, return an error message and redirect to the profile page
            if (file.getSize() > 5 * 1024 * 1024) {
                model.addAttribute("error", "File size should not exceed 5MB");
                return "redirect:/profile";
            }

            // Get the image data and log its length
            byte[] imageData = file.getBytes();
            logger.info("Image data length: " + imageData.length);

            // Get the user's profile picture or create a new one if it doesn't exist
            ProfilePicture profilePicture = user.getProfilePicture();
            if (profilePicture == null) {
                profilePicture = new ProfilePicture();
                profilePicture.setUser(user);
            }

            // Set the image data and save the profile picture
            profilePicture.setPictureData(imageData);
            userService.saveProfilePicture(profilePicture);

            // Determine the MIME type based on the file extension
            String mimeType;
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            switch (extension.toLowerCase()) {
                case "png":
                    mimeType = "data:image/png;base64,";
                    break;
                case "gif":
                    mimeType = "data:image/gif;base64,";
                    break;
                default:
                    mimeType = "data:image/jpeg;base64,";
                    break;
            }

            // Convert the image data to a Base64 string and log its length
            String base64Image = mimeType + Base64.getEncoder().encodeToString(imageData);
            logger.info("Base64 image string length: " + base64Image.length());

            // Add the Base64 string to the model
            model.addAttribute("base64Image", base64Image);
        }

        // Redirect to the profile page
        return "redirect:/profile";
    }

    // Define a POST mapping for "/profile/update" URL
    @PostMapping("/profile/update")
    public String updateProfile(User updatedUser, @AuthenticationPrincipal UserDetails userDetails) {
        // If the user is not authenticated, redirect to the login page
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Find the user
        Optional<User> userOptional = userService.findByUsername(userDetails.getUsername());
        if (userOptional.isPresent()) {
            // If the user is found, update their name and save the user
            User user = userOptional.get();
            user.setName(updatedUser.getName());
            userService.save(user);
        }

        // Redirect to the profile page
        return "redirect:/profile";
    }

    //    @GetMapping("/profilePicture")
    //    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    //        if (userDetails == null) {
    //            return "redirect:/login";
    //        }
    //        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
    //        if (user != null) {
    //            model.addAttribute("user", user);
    //            if (user.getProfilePicture() != null) {
    //                String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture().getPictureData());
    //                model.addAttribute("base64Image", base64Image);
    //            }
    //            model.addAttribute("blogs", user.getBlogs());
    //        }
    //        return "profile";
    //    }
}