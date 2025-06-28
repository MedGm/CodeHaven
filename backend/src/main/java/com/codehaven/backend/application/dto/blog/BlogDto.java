package com.codehaven.backend.application.dto.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogDto {
    
    private Long id;
    private String title;
    private String content;
    private String excerpt;
    private String coverImageUrl;
    private List<String> tags;
    private Integer likesCount;
    private Integer viewsCount;
    private Integer readingTime;
    private String status;
    private Boolean isFeatured;
    private Boolean isLikedByCurrentUser;
    private String authorUsername;
    private String authorName;
    private Integer commentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}
