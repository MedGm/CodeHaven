package com.codehaven.backend.domain.repository;

import com.codehaven.backend.domain.model.Snippet;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SnippetRepository {
    
    Snippet save(Snippet snippet);
    
    Optional<Snippet> findById(Long id);
    
    Page<Snippet> findAll(Pageable pageable);
    
    Page<Snippet> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Snippet> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Page<Snippet> findByUserAndIsPublicTrueOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<Snippet> findByTitleContainingIgnoreCaseAndIsPublicTrue(String title);
    
    List<Snippet> findByLanguageAndIsPublicTrue(String language);
    
    List<Snippet> findByTagsContainingAndIsPublicTrue(String tag);
    
    List<Snippet> findTrendingSnippets();
    
    List<Snippet> findTopSnippetsByLikes();
    
    List<Snippet> findTopSnippetsByViews();
    
    List<Snippet> findRecentPublicSnippets();
    
    List<String> findDistinctLanguages();
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByIdAndUserId(Long snippetId, Long userId);
    
    long count();
    
    long countByUser(User user);
    
    long countByIsPublicTrue();
}
