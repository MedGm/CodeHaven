package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.Snippet;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaSnippetRepository extends JpaRepository<Snippet, Long> {
    
    Page<Snippet> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Snippet> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Page<Snippet> findByUserAndIsPublicTrueOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<Snippet> findByTitleContainingIgnoreCaseAndIsPublicTrue(String title);
    
    List<Snippet> findByLanguageAndIsPublicTrue(String language);
    
    @Query("SELECT s FROM Snippet s WHERE :tag MEMBER OF s.tags AND s.isPublic = true")
    List<Snippet> findByTagsContainingAndIsPublicTrue(@Param("tag") String tag);
    
    List<Snippet> findTop10ByIsPublicTrueOrderByLikesDesc();
    
    List<Snippet> findTop10ByIsPublicTrueOrderByCreatedAtDesc();
    
    List<Snippet> findTop10ByIsPublicTrueOrderByViewsDesc();
    
    @Query("SELECT s FROM Snippet s WHERE s.isPublic = true AND s.createdAt >= :since ORDER BY (s.likes + s.views) DESC")
    List<Snippet> findTrendingSnippets(@Param("since") LocalDateTime since, Pageable pageable);
    
    @Query("SELECT DISTINCT s.language FROM Snippet s WHERE s.isPublic = true ORDER BY s.language")
    List<String> findDistinctLanguages();
    
    boolean existsByIdAndUserId(Long snippetId, Long userId);
    
    long countByUser(User user);
    
    long countByIsPublicTrue();
}
