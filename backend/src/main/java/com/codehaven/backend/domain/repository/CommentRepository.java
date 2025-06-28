package com.codehaven.backend.domain.repository;

import com.codehaven.backend.domain.model.Comment;
import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    
    Comment save(Comment comment);
    
    Optional<Comment> findById(Long id);
    
    Page<Comment> findAllByBlog(Blog blog, Pageable pageable);
    
    Page<Comment> findAllByUser(User user, Pageable pageable);
    
    List<Comment> findAllByParentComment(Comment parentComment);
    
    List<Comment> findRootCommentsByBlog(Blog blog);
    
    void deleteById(Long id);
    
    long count();
    
    long countByBlog(Blog blog);
    
    long countByUser(User user);
    
    Comment incrementLikes(Long commentId);
    
    Comment decrementLikes(Long commentId);
}
