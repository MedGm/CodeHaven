package com.codehaven.backend.domain.repository;

import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(Long id);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByGithubUsername(String githubUsername);
    
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    Page<User> findAll(Pageable pageable);
    
    Page<User> findAllByIsActiveTrue(Pageable pageable);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsById(Long id);
    
    void deleteById(Long id);
    
    long count();
    
    List<User> findTopUsersByProjectCount(int limit);
    
    List<User> findTopUsersByBlogCount(int limit);
}
