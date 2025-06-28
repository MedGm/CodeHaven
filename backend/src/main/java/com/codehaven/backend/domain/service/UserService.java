package com.codehaven.backend.domain.service;

import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    User createUser(User user);
    
    User updateUser(Long userId, User userUpdate);
    
    Optional<User> findById(Long userId);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Page<User> findAllUsers(Pageable pageable);
    
    Page<User> findActiveUsers(Pageable pageable);
    
    List<User> searchUsersByUsername(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    void deactivateUser(Long userId);
    
    void deleteUser(Long userId);
    
    User updateUserProfile(Long userId, String bio, List<String> techStack, String avatarUrl);
    
    User updateUserRole(Long userId, User.Role role);
    
    List<User> getTopContributors();
    
    long getTotalUserCount();
}
