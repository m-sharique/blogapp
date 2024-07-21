package com.example.blogapp.service;

import com.example.blogapp.model.ProfilePicture;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.ProfilePictureRepository;
import com.example.blogapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    //For Profile Picture
    @Autowired
    private ProfilePictureRepository profilePictureRepository;

    public Optional<ProfilePicture> getProfilePictureByUserId(Long userId) {
        return profilePictureRepository.findByUserId(userId);
    }

    public void saveProfilePicture(ProfilePicture profilePicture) {
        profilePictureRepository.save(profilePicture);
    }
}
