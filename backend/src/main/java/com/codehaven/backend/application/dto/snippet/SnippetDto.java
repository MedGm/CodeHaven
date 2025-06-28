package com.codehaven.backend.application.dto.snippet;

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
public class SnippetDto {
    
    private Long id;
    private String title;
    private String description;
    private String code;
    private String language;
    private List<String> tags;
    private Boolean isPublic;
    private Boolean isGist;
    private String gistUrl;
    private Integer likesCount;
    private Integer viewsCount;
    private Boolean isLikedByCurrentUser;
    private String authorUsername;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
