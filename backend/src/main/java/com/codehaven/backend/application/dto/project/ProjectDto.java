package com.codehaven.backend.application.dto.project;

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
public class ProjectDto {
    
    private Long id;
    private String title;
    private String description;
    private String repoUrl;
    private String demoUrl;
    private List<String> technologies;
    private List<String> tags;
    private Boolean isPublic;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User information
    private Long userId;
    private String username;
    private String userAvatarUrl;
    
    // Statistics
    private Long views;
    private Long likes;
    
    // Additional fields
    private List<String> collaborators;
}
