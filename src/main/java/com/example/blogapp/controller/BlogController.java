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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @GetMapping("/create_blog")
    public String createBlogForm(Model model) {
        model.addAttribute("blog", new Blog());
        return "create_blog";
    }

    @PostMapping("/create_blog")
    public String createBlog(Blog blog, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            blog.setUser(user);
            blogService.save(blog);
        }
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("blogs", user.getBlogs());
        }
        return "profile";
    }

    @PostMapping("/archive_blog")
    public String archiveBlog(@RequestParam Long blogId) {
        // Implement archive logic
        return "redirect:/profile";
    }

    @PostMapping("/delete_blog")
    public String deleteBlog(@RequestParam Long blogId) {
        blogService.deleteById(blogId);
        return "redirect:/profile";
    }
}