package com.codehaven.backend.domain.service;

import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.Comment;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    
    Comment createComment(Comment comment);
    
    Comment updateComment(Long commentId, Comment commentUpdate);
    
    Optional<Comment> findById(Long commentId);
    
    Page<Comment> findCommentsByBlog(Blog blog, Pageable pageable);
    
    List<Comment> findCommentsByUser(User user);
    
    List<Comment> findRepliesByParentComment(Comment parentComment);
    
    Comment likeComment(Long commentId);
    
    Comment unlikeComment(Long commentId);
    
    void deleteComment(Long commentId);
    
    long getCommentCountByBlog(Blog blog);
    
    long getCommentCountByUser(User user);
}
