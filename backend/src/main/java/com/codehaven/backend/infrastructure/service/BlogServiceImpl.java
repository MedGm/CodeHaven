package com.codehaven.backend.infrastructure.service;

import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.BlogRepository;
import com.codehaven.backend.domain.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BlogServiceImpl implements BlogService {
    
    private final BlogRepository blogRepository;
    
    @Override
    public Blog createBlog(Blog blog) {
        log.debug("Creating new blog with title: {}", blog.getTitle());
        return blogRepository.save(blog);
    }
    
    @Override
    public Blog updateBlog(Long blogId, Blog blogUpdate) {
        log.debug("Updating blog with ID: {}", blogId);
        
        Blog existingBlog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with ID: " + blogId));
        
        // Update fields
        existingBlog.setTitle(blogUpdate.getTitle());
        existingBlog.setContent(blogUpdate.getContent());
        existingBlog.setExcerpt(blogUpdate.getExcerpt());
        existingBlog.setReadingTime(blogUpdate.getReadingTime());
        existingBlog.setTags(blogUpdate.getTags());
        existingBlog.setStatus(blogUpdate.getStatus());
        existingBlog.setIsFeatured(blogUpdate.getIsFeatured());
        existingBlog.setUpdatedAt(LocalDateTime.now());
        
        if (blogUpdate.getStatus() == Blog.Status.PUBLISHED && existingBlog.getPublishedAt() == null) {
            existingBlog.setPublishedAt(LocalDateTime.now());
        }
        
        return blogRepository.save(existingBlog);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Blog> findById(Long blogId) {
        log.debug("Finding blog by ID: {}", blogId);
        return blogRepository.findById(blogId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Blog> findAllBlogs(Pageable pageable) {
        log.debug("Finding all blogs with pageable: {}", pageable);
        return blogRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Blog> findPublishedBlogs(Pageable pageable) {
        log.debug("Finding published blogs with pageable: {}", pageable);
        return blogRepository.findAllByStatus(Blog.Status.PUBLISHED, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Blog> findBlogsByUser(User user, Pageable pageable) {
        log.debug("Finding blogs by user: {}", user.getUsername());
        return blogRepository.findAllByUser(user, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Blog> findBlogsByUserAndStatus(User user, Blog.Status status, Pageable pageable) {
        log.debug("Finding blogs by user: {} and status: {}", user.getUsername(), status);
        return blogRepository.findAllByUserAndStatus(user, status, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Blog> searchBlogsByTitle(String title) {
        log.debug("Searching blogs by title: {}", title);
        return blogRepository.findByTitleContainingIgnoreCase(title);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Blog> findBlogsByTag(String tag) {
        log.debug("Finding blogs by tag: {}", tag);
        return blogRepository.findByTagsContainingIgnoreCase(tag);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Blog> findFeaturedBlogs(Pageable pageable) {
        log.debug("Finding featured blogs with pageable: {}", pageable);
        return blogRepository.findAllByIsFeaturedTrue(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Blog> getTrendingBlogs() {
        log.debug("Getting trending blogs");
        return blogRepository.findTopByOrderByViewsDesc(10);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Blog> getPopularBlogs() {
        log.debug("Getting popular blogs");
        return blogRepository.findTopByOrderByLikesDesc(10);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Blog> getRecentBlogs() {
        log.debug("Getting recent blogs");
        return blogRepository.findRecentPublishedBlogs(10);
    }
    
    @Override
    public Blog publishBlog(Long blogId) {
        log.debug("Publishing blog with ID: {}", blogId);
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with ID: " + blogId));
        
        blog.setStatus(Blog.Status.PUBLISHED);
        blog.setPublishedAt(LocalDateTime.now());
        
        return blogRepository.save(blog);
    }
    
    @Override
    public Blog unpublishBlog(Long blogId) {
        log.debug("Unpublishing blog with ID: {}", blogId);
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with ID: " + blogId));
        
        blog.setStatus(Blog.Status.DRAFT);
        
        return blogRepository.save(blog);
    }
    
    @Override
    public Blog incrementViews(Long blogId) {
        log.debug("Incrementing views for blog ID: {}", blogId);
        return blogRepository.incrementViews(blogId);
    }
    
    @Override
    public Blog likeBlog(Long blogId) {
        log.debug("Liking blog with ID: {}", blogId);
        return blogRepository.incrementLikes(blogId);
    }
    
    @Override
    public Blog unlikeBlog(Long blogId) {
        log.debug("Unliking blog with ID: {}", blogId);
        return blogRepository.decrementLikes(blogId);
    }
    
    @Override
    public void deleteBlog(Long blogId) {
        log.debug("Deleting blog with ID: {}", blogId);
        blogRepository.deleteById(blogId);
    }
    
    @Override
    public Blog toggleFeatured(Long blogId) {
        log.debug("Toggling featured status for blog ID: {}", blogId);
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with ID: " + blogId));
        
        blog.setIsFeatured(!blog.getIsFeatured());
        
        return blogRepository.save(blog);
    }
    
    @Override
    public long getTotalBlogCount() {
        log.debug("Getting total blog count");
        return blogRepository.count();
    }
    
    @Override
    public long getUserBlogCount(User user) {
        log.debug("Getting blog count for user: {}", user.getUsername());
        return blogRepository.countByUser(user);
    }
    
    @Override
    public long getPublishedBlogCount() {
        log.debug("Getting published blog count");
        return blogRepository.countByStatus(Blog.Status.PUBLISHED);
    }
    
    @Override
    public String generateExcerpt(String content, int maxLength) {
        log.debug("Generating excerpt from content of length: {}", content.length());
        
        if (content == null || content.trim().isEmpty()) {
            return "";
        }
        
        String cleanContent = content.replaceAll("<[^>]*>", "").trim(); // Remove HTML tags
        
        if (cleanContent.length() <= maxLength) {
            return cleanContent;
        }
        
        // Find the last space within the maxLength to avoid cutting words
        int lastSpace = cleanContent.lastIndexOf(' ', maxLength);
        if (lastSpace > 0) {
            return cleanContent.substring(0, lastSpace) + "...";
        }
        
        return cleanContent.substring(0, maxLength) + "...";
    }
    
    @Override
    public Integer calculateReadingTime(String content) {
        log.debug("Calculating reading time for content of length: {}", content.length());
        
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        
        // Remove HTML tags and count words
        String cleanContent = content.replaceAll("<[^>]*>", "").trim();
        String[] words = cleanContent.split("\\s+");
        int wordCount = words.length;
        
        // Average reading speed is about 200-250 words per minute
        // We'll use 200 words per minute for conservative estimation
        int readingTimeMinutes = Math.max(1, (int) Math.ceil(wordCount / 200.0));
        
        return readingTimeMinutes;
    }
}
