package com.codehaven.backend.infrastructure.service;

import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.Comment;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.CommentRepository;
import com.codehaven.backend.domain.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    
    private final CommentRepository commentRepository;
    
    @Override
    public Comment createComment(Comment comment) {
        log.debug("Creating new comment for blog: {}", comment.getBlog().getId());
        return commentRepository.save(comment);
    }
    
    @Override
    public Comment updateComment(Long commentId, Comment commentUpdate) {
        log.debug("Updating comment with ID: {}", commentId);
        
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));
        
        existingComment.setContent(commentUpdate.getContent());
        
        return commentRepository.save(existingComment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(Long commentId) {
        log.debug("Finding comment by ID: {}", commentId);
        return commentRepository.findById(commentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Comment> findCommentsByBlog(Blog blog, Pageable pageable) {
        log.debug("Finding comments for blog: {}", blog.getId());
        return commentRepository.findAllByBlog(blog, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Comment> findCommentsByUser(User user) {
        log.debug("Finding comments by user: {}", user.getUsername());
        // Get all comments by user with default pagination
        Page<Comment> commentPage = commentRepository.findAllByUser(user, Pageable.unpaged());
        return commentPage.getContent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Comment> findRepliesByParentComment(Comment parentComment) {
        log.debug("Finding replies for comment: {}", parentComment.getId());
        return commentRepository.findAllByParentComment(parentComment);
    }
    
    @Override
    public Comment likeComment(Long commentId) {
        log.debug("Liking comment with ID: {}", commentId);
        return commentRepository.incrementLikes(commentId);
    }
    
    @Override
    public Comment unlikeComment(Long commentId) {
        log.debug("Unliking comment with ID: {}", commentId);
        return commentRepository.decrementLikes(commentId);
    }
    
    @Override
    public void deleteComment(Long commentId) {
        log.debug("Deleting comment with ID: {}", commentId);
        commentRepository.deleteById(commentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCommentCountByBlog(Blog blog) {
        log.debug("Getting comment count for blog: {}", blog.getId());
        return commentRepository.countByBlog(blog);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCommentCountByUser(User user) {
        log.debug("Getting comment count for user: {}", user.getUsername());
        return commentRepository.countByUser(user);
    }
}
