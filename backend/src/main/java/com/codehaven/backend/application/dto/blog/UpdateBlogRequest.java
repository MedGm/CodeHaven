package com.codehaven.backend.application.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlogRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    private String excerpt;
    
    private String coverImageUrl;
    
    private List<String> tags;
    
    private Integer readingTime;
    
    private Boolean isFeatured;
    
    private String status; // DRAFT, PUBLISHED, ARCHIVED
}
