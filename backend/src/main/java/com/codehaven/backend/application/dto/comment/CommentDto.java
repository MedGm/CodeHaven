package com.codehaven.backend.application.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    
    private Long id;
    private String content;
    private Integer likesCount;
    private Boolean isEdited;
    private Boolean isLikedByCurrentUser;
    private String authorUsername;
    private String authorName;
    private Long blogId;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
