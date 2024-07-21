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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;
import java.util.Optional;

// Define the class as a Controller in the Spring MVC framework
@Controller
public class BlogController {

    // Autowire the BlogService and UserService
    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    // Define a GET mapping for "/create_blog" URL
    @GetMapping("/create_blog")
    public String createBlogForm(Model model) {
        // Add a new Blog object to the model
        model.addAttribute("blog", new Blog());
        // Return the name of the view to be rendered
        return "create_blog";
    }

    // Define a POST mapping for "/create_blog" URL
    @PostMapping("/create_blog")
    public String createBlog(Blog blog, @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the user is authenticated
        if (userDetails == null) {
            // If not, redirect to the login page
            return "redirect:/login";
        }
        // Find the user by username
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            // If the user is found, set the user to the blog and save the blog
            blog.setUser(user);
            blogService.save(blog);
        }
        // Redirect to the home page
        return "redirect:/";
    }

    // Define a GET mapping for "/profile" URL
    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the user is authenticated
        if (userDetails == null) {
            // If not, redirect to the login page
            return "redirect:/login";
        }
        // Find the user by username
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            // If the user is found, add the user and their blogs to the model
            model.addAttribute("user", user);
            model.addAttribute("blogs", user.getBlogs());
            // If the user has a profile picture, add it to the model as a Base64 string
            if (user.getProfilePicture() != null && user.getProfilePicture().getPictureData() != null) {
                String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture().getPictureData());
                model.addAttribute("base64Image", base64Image);
            }
        }
        // Return the name of the view to be rendered
        return "profile";
    }

    // Define a POST mapping for "/edit_blog" URL
    @PostMapping("/edit_blog")
    public String editBlog(@RequestParam Long id, @RequestParam String title, @RequestParam String content, @AuthenticationPrincipal UserDetails userDetails) {
        // Find the blog by id
        Optional<Blog> blogOptional = blogService.findById(id);
        if (blogOptional.isPresent()) {
            // If the blog is found, get the blog
            Blog blog = blogOptional.get();
            // Find the user by username
            Optional<User> userOptional = userService.findByUsername(userDetails.getUsername());
            if (userOptional.isPresent() && blog.getUser().equals(userOptional.get())) {
                // If the user is found and is the owner of the blog, update the blog and save it
                blog.setTitle(title);
                blog.setContent(content);
                blogService.save(blog);
            }
        }
        // Redirect to the profile page
        return "redirect:/profile";
    }

    // Define a POST mapping for "/delete_blog" URL
    @PostMapping("/delete_blog")
    public String deleteBlog(@RequestParam Long blogId) {
        // Delete the blog by id
        blogService.deleteById(blogId);
        // Redirect to the profile page
        return "redirect:/profile";
    }
}