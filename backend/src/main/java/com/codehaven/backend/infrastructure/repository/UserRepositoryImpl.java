package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    
    private final JpaUserRepository jpaUserRepository;
    
    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }
    
    @Override
    public Optional<User> findByGithubUsername(String githubUsername) {
        return jpaUserRepository.findByGithubUsername(githubUsername);
    }
    
    @Override
    public List<User> findByUsernameContainingIgnoreCase(String username) {
        return jpaUserRepository.findByUsernameContainingIgnoreCase(username);
    }
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        return jpaUserRepository.findAll(pageable);
    }
    
    @Override
    public Page<User> findAllByIsActiveTrue(Pageable pageable) {
        return jpaUserRepository.findAllByIsActiveTrue(pageable);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaUserRepository.existsById(id);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaUserRepository.deleteById(id);
    }
    
    @Override
    public long count() {
        return jpaUserRepository.count();
    }
    
    @Override
    public List<User> findTopUsersByProjectCount(int limit) {
        return jpaUserRepository.findTopUsersByProjectCount(PageRequest.of(0, limit));
    }
    
    @Override
    public List<User> findTopUsersByBlogCount(int limit) {
        return jpaUserRepository.findTopUsersByBlogCount(PageRequest.of(0, limit));
    }
}
