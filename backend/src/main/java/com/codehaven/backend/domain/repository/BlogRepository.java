package com.codehaven.backend.domain.repository;

import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BlogRepository {
    
    Blog save(Blog blog);
    
    Optional<Blog> findById(Long id);
    
    Page<Blog> findAll(Pageable pageable);
    
    Page<Blog> findAllByStatus(Blog.Status status, Pageable pageable);
    
    Page<Blog> findAllByUser(User user, Pageable pageable);
    
    Page<Blog> findAllByUserAndStatus(User user, Blog.Status status, Pageable pageable);
    
    List<Blog> findByTitleContainingIgnoreCase(String title);
    
    List<Blog> findByTagsContainingIgnoreCase(String tag);
    
    Page<Blog> findAllByIsFeaturedTrue(Pageable pageable);
    
    List<Blog> findTopByOrderByLikesDesc(int limit);
    
    List<Blog> findTopByOrderByViewsDesc(int limit);
    
    List<Blog> findRecentPublishedBlogs(int limit);
    
    void deleteById(Long id);
    
    long count();
    
    long countByUser(User user);
    
    long countByStatus(Blog.Status status);
    
    Blog incrementViews(Long blogId);
    
    Blog incrementLikes(Long blogId);
    
    Blog decrementLikes(Long blogId);
}
