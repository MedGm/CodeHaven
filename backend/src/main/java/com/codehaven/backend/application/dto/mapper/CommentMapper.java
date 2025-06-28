package com.codehaven.backend.application.dto.mapper;

import com.codehaven.backend.application.dto.comment.CommentDto;
import com.codehaven.backend.application.dto.comment.CreateCommentRequest;
import com.codehaven.backend.application.dto.comment.UpdateCommentRequest;
import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.Comment;
import com.codehaven.backend.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    
    public Comment toEntity(CreateCommentRequest request, User author, Blog blog, Comment parentComment) {
        return Comment.builder()
                .content(request.getContent())
                .user(author)
                .blog(blog)
                .parentComment(parentComment)
                .likes(0L)
                .isEdited(false)
                .build();
    }
    
    public void updateEntity(Comment comment, UpdateCommentRequest request) {
        comment.setContent(request.getContent());
        comment.setIsEdited(true);
    }
    
    public CommentDto toDto(Comment comment) {
        return toDto(comment, null);
    }
    
    public CommentDto toDto(Comment comment, Set<Long> likedCommentIds) {
        boolean isLikedByCurrentUser = likedCommentIds != null && likedCommentIds.contains(comment.getId());
        
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .likesCount(comment.getLikes().intValue())
                .isEdited(comment.getIsEdited())
                .isLikedByCurrentUser(isLikedByCurrentUser)
                .authorUsername(comment.getUser().getUsername())
                .authorName(comment.getUser().getUsername()) // Using username as display name
                .blogId(comment.getBlog().getId())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
    
    public List<CommentDto> toDtoList(List<Comment> comments) {
        return comments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<CommentDto> toDtoList(List<Comment> comments, Set<Long> likedCommentIds) {
        return comments.stream()
                .map(comment -> toDto(comment, likedCommentIds))
                .collect(Collectors.toList());
    }
}
