package com.codehaven.backend.domain.service;

import com.codehaven.backend.domain.model.Snippet;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SnippetService {
    
    Snippet createSnippet(Snippet snippet);
    
    Snippet updateSnippet(Long snippetId, Snippet snippetUpdate);
    
    Optional<Snippet> findById(Long snippetId);
    
    Page<Snippet> findAllSnippets(Pageable pageable);
    
    Page<Snippet> findPublicSnippets(Pageable pageable);
    
    Page<Snippet> findSnippetsByUser(User user, Pageable pageable);
    
    Page<Snippet> findPublicSnippetsByUser(User user, Pageable pageable);
    
    List<Snippet> searchSnippetsByTitle(String title);
    
    List<Snippet> findSnippetsByLanguage(String language);
    
    List<Snippet> findSnippetsByTag(String tag);
    
    List<Snippet> getTrendingSnippets();
    
    List<Snippet> getPopularSnippets();
    
    List<Snippet> getTopViewedSnippets();
    
    List<Snippet> getRecentSnippets();
    
    List<String> getAllLanguages();
    
    Snippet incrementViews(Long snippetId);
    
    Snippet likeSnippet(Long snippetId);
    
    Snippet unlikeSnippet(Long snippetId);
    
    void deleteSnippet(Long snippetId);
    
    long getTotalSnippetCount();
    
    long getPublicSnippetCount();
    
    long getUserSnippetCount(User user);
}
