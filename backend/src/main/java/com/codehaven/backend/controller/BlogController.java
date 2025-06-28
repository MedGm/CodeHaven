package com.codehaven.backend.controller;

import com.codehaven.backend.application.dto.blog.BlogDto;
import com.codehaven.backend.application.dto.blog.CreateBlogRequest;
import com.codehaven.backend.application.dto.blog.UpdateBlogRequest;
import com.codehaven.backend.application.usecase.BlogManagementUseCase;
import com.codehaven.backend.security.CurrentUser;
import com.codehaven.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@Slf4j
public class BlogController {

    private final BlogManagementUseCase blogManagementUseCase;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BlogDto> createBlog(
            @Valid @RequestBody CreateBlogRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Creating blog for user: {}", currentUser.getUsername());
        
        BlogDto blog = blogManagementUseCase.createBlog(request, currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(blog);
    }

    @PutMapping("/{blogId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BlogDto> updateBlog(
            @PathVariable Long blogId,
            @Valid @RequestBody UpdateBlogRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Updating blog {} for user: {}", blogId, currentUser.getUsername());
        
        BlogDto blog = blogManagementUseCase.updateBlog(blogId, request, currentUser.getUsername());
        return ResponseEntity.ok(blog);
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<BlogDto> getBlogById(
            @PathVariable Long blogId) {
        
        log.info("Fetching blog {}", blogId);
        
        BlogDto blog = blogManagementUseCase.getBlogById(blogId, null);
        return ResponseEntity.ok(blog);
    }

    @DeleteMapping("/{blogId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteBlog(
            @PathVariable Long blogId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Deleting blog {} for user: {}", blogId, currentUser.getUsername());
        
        blogManagementUseCase.deleteBlog(blogId, currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/published")
    public ResponseEntity<Page<BlogDto>> getPublishedBlogs(
            @PageableDefault(size = 20, sort = "publishedAt") Pageable pageable) {
        
        log.info("Fetching published blogs, page: {}", pageable.getPageNumber());
        
        Page<BlogDto> blogs = blogManagementUseCase.getPublishedBlogs(pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<Page<BlogDto>> getUserBlogs(
            @PathVariable String username,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.info("Fetching blogs for user: {}", username);
        
        Page<BlogDto> blogs = blogManagementUseCase.getUserBlogs(username, pageable, null);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BlogDto>> searchBlogsByTitle(
            @RequestParam String title) {
        
        log.info("Searching blogs with title: {}", title);
        
        List<BlogDto> blogs = blogManagementUseCase.searchBlogsByTitle(title);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<BlogDto>> getBlogsByTag(
            @PathVariable String tag) {
        
        log.info("Fetching blogs by tag: {}", tag);
        
        List<BlogDto> blogs = blogManagementUseCase.getBlogsByTag(tag);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/featured")
    public ResponseEntity<Page<BlogDto>> getFeaturedBlogs(
            @PageableDefault(size = 20, sort = "publishedAt") Pageable pageable) {
        
        log.info("Fetching featured blogs");
        
        Page<BlogDto> blogs = blogManagementUseCase.getFeaturedBlogs(pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<BlogDto>> getTrendingBlogs() {
        
        log.info("Fetching trending blogs");
        
        List<BlogDto> blogs = blogManagementUseCase.getTrendingBlogs();
        return ResponseEntity.ok(blogs);
    }

    @PostMapping("/{blogId}/publish")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BlogDto> publishBlog(
            @PathVariable Long blogId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Publishing blog {} for user: {}", blogId, currentUser.getUsername());
        
        BlogDto blog = blogManagementUseCase.publishBlog(blogId, currentUser.getUsername());
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{blogId}/unpublish")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BlogDto> unpublishBlog(
            @PathVariable Long blogId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Unpublishing blog {} for user: {}", blogId, currentUser.getUsername());
        
        BlogDto blog = blogManagementUseCase.unpublishBlog(blogId, currentUser.getUsername());
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{blogId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BlogDto> likeBlog(
            @PathVariable Long blogId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("User {} liking blog {}", currentUser.getUsername(), blogId);
        
        BlogDto blog = blogManagementUseCase.likeBlog(blogId, currentUser.getUsername());
        return ResponseEntity.ok(blog);
    }

    @DeleteMapping("/{blogId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BlogDto> unlikeBlog(
            @PathVariable Long blogId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("User {} unliking blog {}", currentUser.getUsername(), blogId);
        
        BlogDto blog = blogManagementUseCase.unlikeBlog(blogId, currentUser.getUsername());
        return ResponseEntity.ok(blog);
    }

    @PostMapping("/{blogId}/toggle-featured")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BlogDto> toggleFeatured(
            @PathVariable Long blogId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Toggling featured status for blog {} by user: {}", blogId, currentUser.getUsername());
        
        BlogDto blog = blogManagementUseCase.toggleFeatured(blogId, currentUser.getUsername());
        return ResponseEntity.ok(blog);
    }
}
