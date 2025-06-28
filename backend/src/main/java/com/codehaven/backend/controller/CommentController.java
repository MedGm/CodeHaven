package com.codehaven.backend.controller;

import com.codehaven.backend.application.dto.comment.CommentDto;
import com.codehaven.backend.application.dto.comment.CreateCommentRequest;
import com.codehaven.backend.application.dto.comment.UpdateCommentRequest;
import com.codehaven.backend.application.usecase.CommentManagementUseCase;
import com.codehaven.backend.security.CurrentUser;
import com.codehaven.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentManagementUseCase commentManagementUseCase;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentDto> createComment(
            @Valid @RequestBody CreateCommentRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Creating comment for blog {} by user: {}", request.getBlogId(), currentUser.getUsername());
        
        CommentDto comment = commentManagementUseCase.createComment(request, currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Updating comment {} for user: {}", commentId, currentUser.getUsername());
        
        CommentDto comment = commentManagementUseCase.updateComment(commentId, request, currentUser.getUsername());
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(
            @PathVariable Long commentId) {
        
        log.info("Fetching comment {}", commentId);
        
        CommentDto comment = commentManagementUseCase.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Deleting comment {} for user: {}", commentId, currentUser.getUsername());
        
        commentManagementUseCase.deleteComment(commentId, currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blog/{blogId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByBlog(
            @PathVariable Long blogId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.info("Fetching comments for blog: {}", blogId);
        
        Page<CommentDto> comments = commentManagementUseCase.getCommentsByBlog(blogId, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentDto>> getUserComments(
            @PathVariable String username) {
        
        log.info("Fetching comments for user: {}", username);
        
        List<CommentDto> comments = commentManagementUseCase.getUserComments(username);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentDto>> getReplies(
            @PathVariable Long commentId) {
        
        log.info("Fetching replies for comment: {}", commentId);
        
        List<CommentDto> replies = commentManagementUseCase.getReplies(commentId);
        return ResponseEntity.ok(replies);
    }

    @PostMapping("/{commentId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentDto> likeComment(
            @PathVariable Long commentId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("User {} liking comment {}", currentUser.getUsername(), commentId);
        
        CommentDto comment = commentManagementUseCase.likeComment(commentId, currentUser.getUsername());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentDto> unlikeComment(
            @PathVariable Long commentId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("User {} unliking comment {}", currentUser.getUsername(), commentId);
        
        CommentDto comment = commentManagementUseCase.unlikeComment(commentId, currentUser.getUsername());
        return ResponseEntity.ok(comment);
    }
}
