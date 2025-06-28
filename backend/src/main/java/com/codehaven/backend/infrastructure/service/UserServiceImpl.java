package com.codehaven.backend.infrastructure.service;

import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.UserRepository;
import com.codehaven.backend.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public User createUser(User user) {
        log.info("Creating new user with username: {}", user.getUsername());
        
        // Validate unique constraints
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        // Encode password if provided
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // Set timestamps (using @CreationTimestamp and @UpdateTimestamp annotations)
        user.setIsActive(true);
        
        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(Long userId, User userUpdate) {
        log.info("Updating user with ID: {}", userId);
        
        User existingUser = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        // Update only non-null fields
        if (userUpdate.getUsername() != null && !userUpdate.getUsername().equals(existingUser.getUsername())) {
            if (existsByUsername(userUpdate.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + userUpdate.getUsername());
            }
            existingUser.setUsername(userUpdate.getUsername());
        }
        
        if (userUpdate.getEmail() != null && !userUpdate.getEmail().equals(existingUser.getEmail())) {
            if (existsByEmail(userUpdate.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + userUpdate.getEmail());
            }
            existingUser.setEmail(userUpdate.getEmail());
        }
        
        if (userUpdate.getBio() != null) {
            existingUser.setBio(userUpdate.getBio());
        }
        
        if (userUpdate.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(userUpdate.getAvatarUrl());
        }
        
        if (userUpdate.getGithubUsername() != null) {
            existingUser.setGithubUsername(userUpdate.getGithubUsername());
        }
        
        // updatedAt is handled by @UpdateTimestamp
        return userRepository.save(existingUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> findActiveUsers(Pageable pageable) {
        return userRepository.findAllByIsActiveTrue(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public void deactivateUser(Long userId) {
        log.info("Deactivating user with ID: {}", userId);
        
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        
        userRepository.deleteById(userId);
    }
    
    @Override
    public User updateUserProfile(Long userId, String bio, List<String> techStack, String avatarUrl) {
        log.info("Updating profile for user with ID: {}", userId);
        
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        if (bio != null) {
            user.setBio(bio);
        }
        
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    @Override
    public User updateUserRole(Long userId, User.Role role) {
        log.info("Updating role for user with ID: {} to: {}", userId, role);
        
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        user.setRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getTopContributors() {
        // Return active users by projects (using available repository methods)
        return userRepository.findTopUsersByProjectCount(10);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public User changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Changing password for user with ID: {}", userId);
        
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        if (!checkPassword(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
