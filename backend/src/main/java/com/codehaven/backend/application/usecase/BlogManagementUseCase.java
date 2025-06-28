package com.codehaven.backend.application.usecase;

import com.codehaven.backend.application.dto.blog.BlogDto;
import com.codehaven.backend.application.dto.blog.CreateBlogRequest;
import com.codehaven.backend.application.dto.blog.UpdateBlogRequest;
import com.codehaven.backend.application.dto.mapper.BlogMapper;
import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.service.BlogService;
import com.codehaven.backend.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BlogManagementUseCase {

    private final BlogService blogService;
    private final UserService userService;
    private final BlogMapper blogMapper;

    public BlogDto createBlog(CreateBlogRequest request, String username) {
        log.info("Creating blog for user: {}", username);
        
        User author = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Auto-generate excerpt if not provided
        if (request.getExcerpt() == null || request.getExcerpt().trim().isEmpty()) {
            request.setExcerpt(blogService.generateExcerpt(request.getContent(), 200));
        }
        
        // Auto-calculate reading time if not provided
        if (request.getReadingTime() == null) {
            request.setReadingTime(blogService.calculateReadingTime(request.getContent()));
        }
        
        Blog blog = blogMapper.toEntity(request, author);
        Blog savedBlog = blogService.createBlog(blog);
        
        log.info("Blog created with ID: {}", savedBlog.getId());
        return blogMapper.toDto(savedBlog);
    }

    public BlogDto updateBlog(Long blogId, UpdateBlogRequest request, String username) {
        log.info("Updating blog {} for user: {}", blogId, username);
        
        Blog blog = blogService.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Check if user owns the blog
        if (!blog.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own blogs");
        }
        
        // Auto-generate excerpt if not provided
        if (request.getExcerpt() == null || request.getExcerpt().trim().isEmpty()) {
            request.setExcerpt(blogService.generateExcerpt(request.getContent(), 200));
        }
        
        // Auto-calculate reading time if not provided
        if (request.getReadingTime() == null) {
            request.setReadingTime(blogService.calculateReadingTime(request.getContent()));
        }
        
        blogMapper.updateEntity(blog, request);
        Blog updatedBlog = blogService.updateBlog(blogId, blog);
        
        log.info("Blog {} updated successfully", blogId);
        return blogMapper.toDto(updatedBlog);
    }

    @Transactional
    public BlogDto getBlogById(Long blogId, String currentUsername) {
        log.info("Fetching blog: {}", blogId);
        
        Blog blog = blogService.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));
        
        // Check if blog is published or user owns it
        if (blog.getStatus() != Blog.Status.PUBLISHED && 
            (currentUsername == null || !blog.getUser().getUsername().equals(currentUsername))) {
            throw new AccessDeniedException("This blog is not published");
        }
        
        // Increment view count if viewing others' blogs
        if (currentUsername == null || !blog.getUser().getUsername().equals(currentUsername)) {
            blogService.incrementViews(blogId);
        }
        
        return blogMapper.toDto(blog);
    }

    public void deleteBlog(Long blogId, String username) {
        log.info("Deleting blog {} for user: {}", blogId, username);
        
        Blog blog = blogService.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Check if user owns the blog
        if (!blog.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only delete your own blogs");
        }
        
        blogService.deleteBlog(blogId);
        log.info("Blog {} deleted successfully", blogId);
    }

    @Transactional(readOnly = true)
    public Page<BlogDto> getPublishedBlogs(Pageable pageable) {
        log.info("Fetching published blogs, page: {}", pageable.getPageNumber());
        
        Page<Blog> blogs = blogService.findPublishedBlogs(pageable);
        List<BlogDto> blogDtos = blogMapper.toDtoList(blogs.getContent());
        return new PageImpl<>(blogDtos, pageable, blogs.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<BlogDto> getUserBlogs(String targetUsername, Pageable pageable, String currentUsername) {
        log.info("Fetching blogs for user: {}", targetUsername);
        
        User targetUser = userService.findByUsername(targetUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + targetUsername));
        boolean isOwnProfile = currentUsername != null && currentUsername.equals(targetUsername);
        
        Page<Blog> blogs;
        if (isOwnProfile) {
            // Show all blogs for own profile
            blogs = blogService.findBlogsByUser(targetUser, pageable);
        } else {
            // Show only published blogs for other profiles
            blogs = blogService.findBlogsByUserAndStatus(targetUser, Blog.Status.PUBLISHED, pageable);
        }
        
        List<BlogDto> blogDtos = blogMapper.toDtoList(blogs.getContent());
        return new PageImpl<>(blogDtos, pageable, blogs.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<BlogDto> searchBlogsByTitle(String title) {
        log.info("Searching blogs with title: {}", title);
        
        List<Blog> blogs = blogService.searchBlogsByTitle(title);
        return blogMapper.toDtoList(blogs);
    }

    @Transactional(readOnly = true)
    public List<BlogDto> getBlogsByTag(String tag) {
        log.info("Fetching blogs by tag: {}", tag);
        
        List<Blog> blogs = blogService.findBlogsByTag(tag);
        return blogMapper.toDtoList(blogs);
    }

    @Transactional(readOnly = true)
    public Page<BlogDto> getFeaturedBlogs(Pageable pageable) {
        log.info("Fetching featured blogs");
        
        Page<Blog> blogs = blogService.findFeaturedBlogs(pageable);
        List<BlogDto> blogDtos = blogMapper.toDtoList(blogs.getContent());
        return new PageImpl<>(blogDtos, pageable, blogs.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<BlogDto> getTrendingBlogs() {
        log.info("Fetching trending blogs");
        
        List<Blog> blogs = blogService.getTrendingBlogs();
        return blogMapper.toDtoList(blogs);
    }

    public BlogDto publishBlog(Long blogId, String username) {
        log.info("Publishing blog {} for user: {}", blogId, username);
        
        Blog blog = blogService.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Check if user owns the blog
        if (!blog.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only publish your own blogs");
        }
        
        Blog publishedBlog = blogService.publishBlog(blogId);
        log.info("Blog {} published successfully", blogId);
        return blogMapper.toDto(publishedBlog);
    }

    public BlogDto unpublishBlog(Long blogId, String username) {
        log.info("Unpublishing blog {} for user: {}", blogId, username);
        
        Blog blog = blogService.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Check if user owns the blog
        if (!blog.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only unpublish your own blogs");
        }
        
        Blog unpublishedBlog = blogService.unpublishBlog(blogId);
        log.info("Blog {} unpublished successfully", blogId);
        return blogMapper.toDto(unpublishedBlog);
    }

    public BlogDto likeBlog(Long blogId, String username) {
        log.info("User {} liking blog {}", username, blogId);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        Blog blog = blogService.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));
        
        // Check if blog is published or user owns it
        if (blog.getStatus() != Blog.Status.PUBLISHED && !blog.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot like an unpublished blog");
        }
        
        Blog updatedBlog = blogService.likeBlog(blogId);
        log.info("Blog {} liked by user {}", blogId, username);
        return blogMapper.toDto(updatedBlog);
    }

    public BlogDto unlikeBlog(Long blogId, String username) {
        log.info("User {} unliking blog {}", username, blogId);
        
        // Verify user exists
        userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Blog updatedBlog = blogService.unlikeBlog(blogId);
        log.info("Blog {} unliked by user {}", blogId, username);
        return blogMapper.toDto(updatedBlog);
    }

    public BlogDto toggleFeatured(Long blogId, String username) {
        log.info("Toggling featured status for blog {} by user: {}", blogId, username);
        
        Blog blog = blogService.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Check if user owns the blog (or is admin - could add admin check here)
        if (!blog.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only toggle featured status for your own blogs");
        }
        
        Blog updatedBlog = blogService.toggleFeatured(blogId);
        log.info("Blog {} featured status toggled", blogId);
        return blogMapper.toDto(updatedBlog);
    }
}
