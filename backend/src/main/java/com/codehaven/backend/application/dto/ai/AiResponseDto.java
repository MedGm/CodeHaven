package com.codehaven.backend.application.dto.ai;

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
public class AiResponseDto {
    
    private Long id;
    private String requestType; // CODE_REVIEW, BUG_FIX, OPTIMIZATION, EXPLANATION, GENERATION
    private String response;
    private String originalCode;
    private String suggestedCode; // For bug fixes and optimizations
    private List<String> suggestions;
    private List<String> issues;
    private Integer confidenceScore; // 0-100
    private String language;
    private String username;
    private LocalDateTime createdAt;
    private Long processingTimeMs;
}
