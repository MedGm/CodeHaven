package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByGithubUsername(String githubUsername);
    
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    Page<User> findAllByIsActiveTrue(Pageable pageable);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN u.projects p GROUP BY u ORDER BY COUNT(p) DESC")
    List<User> findTopUsersByProjectCount(Pageable pageable);
    
    @Query("SELECT u FROM User u LEFT JOIN u.blogs b GROUP BY u ORDER BY COUNT(b) DESC")
    List<User> findTopUsersByBlogCount(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "(SIZE(u.projects) > 0 OR SIZE(u.blogs) > 0 OR SIZE(u.snippets) > 0)")
    List<User> findActiveContributors(Pageable pageable);
}
