package com.codehaven.backend.infrastructure.service;

import com.codehaven.backend.domain.model.Snippet;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.SnippetRepository;
import com.codehaven.backend.domain.repository.UserRepository;
import com.codehaven.backend.domain.service.SnippetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SnippetServiceImpl implements SnippetService {
    
    private final SnippetRepository snippetRepository;
    private final UserRepository userRepository;
    
    @Override
    public Snippet createSnippet(Snippet snippet) {
        log.info("Creating new snippet '{}'", snippet.getTitle());
        return snippetRepository.save(snippet);
    }
    
    @Override
    public Snippet updateSnippet(Long snippetId, Snippet snippetUpdate) {
        log.info("Updating snippet ID: {}", snippetId);
        
        Snippet existingSnippet = findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found with ID: " + snippetId));
        
        // Update only non-null fields
        if (snippetUpdate.getTitle() != null) {
            existingSnippet.setTitle(snippetUpdate.getTitle());
        }
        
        if (snippetUpdate.getDescription() != null) {
            existingSnippet.setDescription(snippetUpdate.getDescription());
        }
        
        if (snippetUpdate.getCode() != null) {
            existingSnippet.setCode(snippetUpdate.getCode());
        }
        
        if (snippetUpdate.getLanguage() != null) {
            existingSnippet.setLanguage(snippetUpdate.getLanguage());
        }
        
        if (snippetUpdate.getTags() != null) {
            existingSnippet.setTags(snippetUpdate.getTags());
        }
        
        if (snippetUpdate.getIsPublic() != null) {
            existingSnippet.setIsPublic(snippetUpdate.getIsPublic());
        }
        
        if (snippetUpdate.getIsGist() != null) {
            existingSnippet.setIsGist(snippetUpdate.getIsGist());
        }
        
        if (snippetUpdate.getGistUrl() != null) {
            existingSnippet.setGistUrl(snippetUpdate.getGistUrl());
        }
        
        return snippetRepository.save(existingSnippet);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Snippet> findById(Long snippetId) {
        return snippetRepository.findById(snippetId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Snippet> findAllSnippets(Pageable pageable) {
        return snippetRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Snippet> findPublicSnippets(Pageable pageable) {
        return snippetRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Snippet> findSnippetsByUser(User user, Pageable pageable) {
        return snippetRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Snippet> findPublicSnippetsByUser(User user, Pageable pageable) {
        return snippetRepository.findByUserAndIsPublicTrueOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Snippet> searchSnippetsByTitle(String title) {
        return snippetRepository.findByTitleContainingIgnoreCaseAndIsPublicTrue(title);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Snippet> findSnippetsByLanguage(String language) {
        return snippetRepository.findByLanguageAndIsPublicTrue(language);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Snippet> findSnippetsByTag(String tag) {
        return snippetRepository.findByTagsContainingAndIsPublicTrue(tag);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Snippet> getTrendingSnippets() {
        return snippetRepository.findTrendingSnippets();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Snippet> getPopularSnippets() {
        return snippetRepository.findTopSnippetsByLikes();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Snippet> getTopViewedSnippets() {
        return snippetRepository.findTopSnippetsByViews();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Snippet> getRecentSnippets() {
        return snippetRepository.findRecentPublicSnippets();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllLanguages() {
        return snippetRepository.findDistinctLanguages();
    }
    
    @Override
    public Snippet incrementViews(Long snippetId) {
        log.debug("Incrementing view count for snippet ID: {}", snippetId);
        
        Snippet snippet = findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found with ID: " + snippetId));
        
        snippet.setViews(snippet.getViews() + 1);
        return snippetRepository.save(snippet);
    }
    
    @Override
    public Snippet likeSnippet(Long snippetId) {
        log.debug("Liking snippet ID: {}", snippetId);
        
        Snippet snippet = findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found with ID: " + snippetId));
        
        snippet.setLikes(snippet.getLikes() + 1);
        return snippetRepository.save(snippet);
    }
    
    @Override
    public Snippet unlikeSnippet(Long snippetId) {
        log.debug("Unliking snippet ID: {}", snippetId);
        
        Snippet snippet = findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found with ID: " + snippetId));
        
        if (snippet.getLikes() > 0) {
            snippet.setLikes(snippet.getLikes() - 1);
        }
        return snippetRepository.save(snippet);
    }
    
    @Override
    public void deleteSnippet(Long snippetId) {
        log.info("Deleting snippet ID: {}", snippetId);
        
        if (!snippetRepository.existsById(snippetId)) {
            throw new IllegalArgumentException("Snippet not found with ID: " + snippetId);
        }
        
        snippetRepository.deleteById(snippetId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalSnippetCount() {
        return snippetRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getPublicSnippetCount() {
        return snippetRepository.countByIsPublicTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUserSnippetCount(User user) {
        return snippetRepository.countByUser(user);
    }
    
    // Additional utility methods
    public Snippet createSnippetForUser(Snippet snippet, Long userId) {
        log.info("Creating new snippet '{}' for user ID: {}", snippet.getTitle(), userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        snippet.setUser(user);
        return snippetRepository.save(snippet);
    }
    
    public Snippet updateSnippetForUser(Long snippetId, Snippet snippetUpdate, Long userId) {
        log.info("Updating snippet ID: {} by user ID: {}", snippetId, userId);
        
        Snippet existingSnippet = findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found with ID: " + snippetId));
        
        // Check if user owns the snippet
        if (!existingSnippet.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to update this snippet");
        }
        
        return updateSnippet(snippetId, snippetUpdate);
    }
    
    public void deleteSnippetForUser(Long snippetId, Long userId) {
        log.info("Deleting snippet ID: {} by user ID: {}", snippetId, userId);
        
        Snippet snippet = findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found with ID: " + snippetId));
        
        // Check if user owns the snippet
        if (!snippet.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to delete this snippet");
        }
        
        deleteSnippet(snippetId);
    }
    
    public boolean isSnippetOwnedByUser(Long snippetId, Long userId) {
        return snippetRepository.existsByIdAndUserId(snippetId, userId);
    }
}
