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
public class BugFixRequest {
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;
    
    @NotBlank(message = "Error description is required")
    @Size(max = 1000, message = "Error description must not exceed 1000 characters")
    private String errorDescription;
    
    @Size(max = 500, message = "Expected behavior must not exceed 500 characters")
    private String expectedBehavior; // What the user expects the code to do
    
    @Size(max = 500, message = "Context must not exceed 500 characters")
    private String context; // Additional context about the code
}
