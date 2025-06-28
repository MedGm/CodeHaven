package com.codehaven.backend.application.dto.mapper;

import com.codehaven.backend.application.dto.blog.BlogDto;
import com.codehaven.backend.application.dto.blog.CreateBlogRequest;
import com.codehaven.backend.application.dto.blog.UpdateBlogRequest;
import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BlogMapper {
    
    public Blog toEntity(CreateBlogRequest request, User author) {
        Blog.Status status = Blog.Status.DRAFT;
        if (request.getStatus() != null) {
            try {
                status = Blog.Status.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                status = Blog.Status.DRAFT;
            }
        }
        
        return Blog.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .excerpt(request.getExcerpt())
                .coverImageUrl(request.getCoverImageUrl())
                .tags(request.getTags())
                .readingTime(request.getReadingTime())
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .status(status)
                .user(author)
                .likes(0L)
                .views(0L)
                .publishedAt(status == Blog.Status.PUBLISHED ? LocalDateTime.now() : null)
                .build();
    }
    
    public void updateEntity(Blog blog, UpdateBlogRequest request) {
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setExcerpt(request.getExcerpt());
        blog.setCoverImageUrl(request.getCoverImageUrl());
        blog.setTags(request.getTags());
        blog.setReadingTime(request.getReadingTime());
        blog.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        
        if (request.getStatus() != null) {
            try {
                Blog.Status newStatus = Blog.Status.valueOf(request.getStatus().toUpperCase());
                Blog.Status oldStatus = blog.getStatus();
                blog.setStatus(newStatus);
                
                // Set published date if changing from non-published to published
                if (oldStatus != Blog.Status.PUBLISHED && newStatus == Blog.Status.PUBLISHED) {
                    blog.setPublishedAt(LocalDateTime.now());
                }
            } catch (IllegalArgumentException e) {
                // Keep existing status if invalid
            }
        }
    }
    
    public BlogDto toDto(Blog blog) {
        return toDto(blog, null, false);
    }
    
    public BlogDto toDto(Blog blog, Set<Long> likedBlogIds, boolean includePrivateInfo) {
        boolean isLikedByCurrentUser = likedBlogIds != null && likedBlogIds.contains(blog.getId());
        
        return BlogDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .excerpt(blog.getExcerpt())
                .coverImageUrl(blog.getCoverImageUrl())
                .tags(blog.getTags())
                .likesCount(blog.getLikes().intValue())
                .viewsCount(blog.getViews().intValue())
                .readingTime(blog.getReadingTime())
                .status(blog.getStatus().name())
                .isFeatured(blog.getIsFeatured())
                .isLikedByCurrentUser(isLikedByCurrentUser)
                .authorUsername(blog.getUser().getUsername())
                .authorName(blog.getUser().getUsername()) // Using username as display name
                .commentsCount(blog.getComments() != null ? blog.getComments().size() : 0)
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .publishedAt(blog.getPublishedAt())
                .build();
    }
    
    public List<BlogDto> toDtoList(List<Blog> blogs) {
        return blogs.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<BlogDto> toDtoList(List<Blog> blogs, Set<Long> likedBlogIds) {
        return blogs.stream()
                .map(blog -> toDto(blog, likedBlogIds, false))
                .collect(Collectors.toList());
    }
}
