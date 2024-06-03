package com.example.blogapp.repository;

import com.example.blogapp.model.Blog;
import com.example.blogapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByUserIn(List<User> users);
}
