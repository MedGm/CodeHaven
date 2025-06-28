package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.Comment;
import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaCommentRepository extends JpaRepository<Comment, Long> {
    
    Page<Comment> findAllByBlog(Blog blog, Pageable pageable);
    
    Page<Comment> findAllByUser(User user, Pageable pageable);
    
    List<Comment> findAllByParentComment(Comment parentComment);
    
    @Query("SELECT c FROM Comment c WHERE c.blog = :blog AND c.parentComment IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findRootCommentsByBlog(@Param("blog") Blog blog);
    
    long countByBlog(Blog blog);
    
    long countByUser(User user);
    
    @Modifying
    @Query("UPDATE Comment c SET c.likes = c.likes + 1 WHERE c.id = :commentId")
    void incrementLikes(@Param("commentId") Long commentId);
    
    @Modifying
    @Query("UPDATE Comment c SET c.likes = c.likes - 1 WHERE c.id = :commentId AND c.likes > 0")
    void decrementLikes(@Param("commentId") Long commentId);
}
