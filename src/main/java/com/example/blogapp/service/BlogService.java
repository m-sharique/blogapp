package com.example.blogapp.service;

import com.example.blogapp.model.Blog;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public void save(Blog blog) {
        blog.setCreatedAt(LocalDateTime.now());
        blogRepository.save(blog);
    }

    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    public List<Blog> findByUsers(List<User> users) {
        return blogRepository.findByUserIn(users);
    }

    public void deleteById(Long id) {
        blogRepository.deleteById(id);
    }
}
