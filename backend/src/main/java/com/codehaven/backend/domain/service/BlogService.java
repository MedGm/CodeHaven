package com.codehaven.backend.domain.service;

import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BlogService {
    
    Blog createBlog(Blog blog);
    
    Blog updateBlog(Long blogId, Blog blogUpdate);
    
    Optional<Blog> findById(Long blogId);
    
    Page<Blog> findAllBlogs(Pageable pageable);
    
    Page<Blog> findPublishedBlogs(Pageable pageable);
    
    Page<Blog> findBlogsByUser(User user, Pageable pageable);
    
    Page<Blog> findBlogsByUserAndStatus(User user, Blog.Status status, Pageable pageable);
    
    List<Blog> searchBlogsByTitle(String title);
    
    List<Blog> findBlogsByTag(String tag);
    
    Page<Blog> findFeaturedBlogs(Pageable pageable);
    
    List<Blog> getTrendingBlogs();
    
    List<Blog> getPopularBlogs();
    
    List<Blog> getRecentBlogs();
    
    Blog publishBlog(Long blogId);
    
    Blog unpublishBlog(Long blogId);
    
    Blog incrementViews(Long blogId);
    
    Blog likeBlog(Long blogId);
    
    Blog unlikeBlog(Long blogId);
    
    void deleteBlog(Long blogId);
    
    Blog toggleFeatured(Long blogId);
    
    Integer calculateReadingTime(String content);
    
    String generateExcerpt(String content, int maxLength);
    
    long getTotalBlogCount();
    
    long getUserBlogCount(User user);
    
    long getPublishedBlogCount();
}
