package com.codehaven.backend.application.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeOptimizationRequest {
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;
    
    @Size(max = 500, message = "Context must not exceed 500 characters")
    private String context;
    
    private String optimizationType; // PERFORMANCE, MEMORY, READABILITY, MAINTAINABILITY
}
