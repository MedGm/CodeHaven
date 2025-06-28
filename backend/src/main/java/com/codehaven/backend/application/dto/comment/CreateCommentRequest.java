package com.codehaven.backend.application.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Blog ID is required")
    private Long blogId;
    
    private Long parentCommentId; // For replies
}
