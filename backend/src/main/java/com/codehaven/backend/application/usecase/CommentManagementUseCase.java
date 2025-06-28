package com.codehaven.backend.application.usecase;

import com.codehaven.backend.application.dto.comment.CommentDto;
import com.codehaven.backend.application.dto.comment.CreateCommentRequest;
import com.codehaven.backend.application.dto.comment.UpdateCommentRequest;
import com.codehaven.backend.application.dto.mapper.CommentMapper;
import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.Comment;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.service.BlogService;
import com.codehaven.backend.domain.service.CommentService;
import com.codehaven.backend.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentManagementUseCase {

    private final CommentService commentService;
    private final BlogService blogService;
    private final UserService userService;
    private final CommentMapper commentMapper;

    public CommentDto createComment(CreateCommentRequest request, String username) {
        log.info("Creating comment for blog {} by user: {}", request.getBlogId(), username);
        
        User author = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Blog blog = blogService.findById(request.getBlogId())
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + request.getBlogId()));
        
        // Check if blog is published
        if (blog.getStatus() != Blog.Status.PUBLISHED) {
            throw new AccessDeniedException("Cannot comment on unpublished blog");
        }
        
        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentService.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found: " + request.getParentCommentId()));
            
            // Ensure parent comment belongs to the same blog
            if (!parentComment.getBlog().getId().equals(request.getBlogId())) {
                throw new IllegalArgumentException("Parent comment does not belong to the specified blog");
            }
        }
        
        Comment comment = commentMapper.toEntity(request, author, blog, parentComment);
        Comment savedComment = commentService.createComment(comment);
        
        log.info("Comment created with ID: {}", savedComment.getId());
        return commentMapper.toDto(savedComment);
    }

    public CommentDto updateComment(Long commentId, UpdateCommentRequest request, String username) {
        log.info("Updating comment {} for user: {}", commentId, username);
        
        Comment comment = commentService.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Check if user owns the comment
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own comments");
        }
        
        commentMapper.updateEntity(comment, request);
        Comment updatedComment = commentService.updateComment(commentId, comment);
        
        log.info("Comment {} updated successfully", commentId);
        return commentMapper.toDto(updatedComment);
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        log.info("Fetching comment: {}", commentId);
        
        Comment comment = commentService.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        
        return commentMapper.toDto(comment);
    }

    public void deleteComment(Long commentId, String username) {
        log.info("Deleting comment {} for user: {}", commentId, username);
        
        Comment comment = commentService.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Check if user owns the comment
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only delete your own comments");
        }
        
        commentService.deleteComment(commentId);
        log.info("Comment {} deleted successfully", commentId);
    }

    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsByBlog(Long blogId, Pageable pageable) {
        log.info("Fetching comments for blog: {}", blogId);
        
        Blog blog = blogService.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: " + blogId));
        
        Page<Comment> comments = commentService.findCommentsByBlog(blog, pageable);
        List<CommentDto> commentDtos = commentMapper.toDtoList(comments.getContent());
        return new PageImpl<>(commentDtos, pageable, comments.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(String username) {
        log.info("Fetching comments for user: {}", username);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        List<Comment> comments = commentService.findCommentsByUser(user);
        return commentMapper.toDtoList(comments);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getReplies(Long parentCommentId) {
        log.info("Fetching replies for comment: {}", parentCommentId);
        
        Comment parentComment = commentService.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found: " + parentCommentId));
        
        List<Comment> replies = commentService.findRepliesByParentComment(parentComment);
        return commentMapper.toDtoList(replies);
    }

    public CommentDto likeComment(Long commentId, String username) {
        log.info("User {} liking comment {}", username, commentId);
        
        // Verify user exists
        userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Comment comment = commentService.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        
        // Check if the blog is published
        if (comment.getBlog().getStatus() != Blog.Status.PUBLISHED) {
            throw new AccessDeniedException("Cannot like comments on unpublished blogs");
        }
        
        Comment updatedComment = commentService.likeComment(commentId);
        log.info("Comment {} liked by user {}", commentId, username);
        return commentMapper.toDto(updatedComment);
    }

    public CommentDto unlikeComment(Long commentId, String username) {
        log.info("User {} unliking comment {}", username, commentId);
        
        // Verify user exists
        userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Comment updatedComment = commentService.unlikeComment(commentId);
        log.info("Comment {} unliked by user {}", commentId, username);
        return commentMapper.toDto(updatedComment);
    }
}
