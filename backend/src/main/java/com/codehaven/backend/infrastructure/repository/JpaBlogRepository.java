package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaBlogRepository extends JpaRepository<Blog, Long> {
    
    Page<Blog> findAllByStatus(Blog.Status status, Pageable pageable);
    
    Page<Blog> findAllByUser(User user, Pageable pageable);
    
    Page<Blog> findAllByUserAndStatus(User user, Blog.Status status, Pageable pageable);
    
    List<Blog> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT b FROM Blog b WHERE :tag MEMBER OF b.tags")
    List<Blog> findByTagsContaining(@Param("tag") String tag);
    
    Page<Blog> findAllByIsFeaturedTrue(Pageable pageable);
    
    List<Blog> findTop10ByOrderByLikesDesc();
    
    List<Blog> findTop10ByOrderByViewsDesc();
    
    @Query("SELECT b FROM Blog b WHERE b.status = 'PUBLISHED' ORDER BY b.publishedAt DESC")
    List<Blog> findRecentPublishedBlogs(Pageable pageable);
    
    long countByUser(User user);
    
    long countByStatus(Blog.Status status);
    
    @Modifying
    @Query("UPDATE Blog b SET b.views = b.views + 1 WHERE b.id = :blogId")
    void incrementViews(@Param("blogId") Long blogId);
    
    @Modifying
    @Query("UPDATE Blog b SET b.likes = b.likes + 1 WHERE b.id = :blogId")
    void incrementLikes(@Param("blogId") Long blogId);
    
    @Modifying
    @Query("UPDATE Blog b SET b.likes = b.likes - 1 WHERE b.id = :blogId AND b.likes > 0")
    void decrementLikes(@Param("blogId") Long blogId);
    
    @Query("SELECT b FROM Blog b WHERE b.status = 'PUBLISHED' AND b.publishedAt >= :since ORDER BY (b.likes + b.views) DESC")
    List<Blog> findTrendingBlogs(@Param("since") LocalDateTime since, Pageable pageable);
}
