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
public class CodeGenerationRequest {
    
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;
    
    @Size(max = 500, message = "Requirements must not exceed 500 characters")
    private String requirements; // Specific requirements or constraints
    
    private String complexity; // SIMPLE, MEDIUM, COMPLEX
    
    private String codeType; // FUNCTION, CLASS, API, COMPONENT
}
