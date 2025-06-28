package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.Snippet;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.SnippetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SnippetRepositoryImpl implements SnippetRepository {
    
    private final JpaSnippetRepository jpaSnippetRepository;
    
    @Override
    public Snippet save(Snippet snippet) {
        return jpaSnippetRepository.save(snippet);
    }
    
    @Override
    public Optional<Snippet> findById(Long id) {
        return jpaSnippetRepository.findById(id);
    }
    
    @Override
    public Page<Snippet> findAll(Pageable pageable) {
        return jpaSnippetRepository.findAll(pageable);
    }
    
    @Override
    public Page<Snippet> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable) {
        return jpaSnippetRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }
    
    @Override
    public Page<Snippet> findByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
        return jpaSnippetRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    public Page<Snippet> findByUserAndIsPublicTrueOrderByCreatedAtDesc(User user, Pageable pageable) {
        return jpaSnippetRepository.findByUserAndIsPublicTrueOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    public List<Snippet> findByTitleContainingIgnoreCaseAndIsPublicTrue(String title) {
        return jpaSnippetRepository.findByTitleContainingIgnoreCaseAndIsPublicTrue(title);
    }
    
    @Override
    public List<Snippet> findByLanguageAndIsPublicTrue(String language) {
        return jpaSnippetRepository.findByLanguageAndIsPublicTrue(language);
    }
    
    @Override
    public List<Snippet> findByTagsContainingAndIsPublicTrue(String tag) {
        return jpaSnippetRepository.findByTagsContainingAndIsPublicTrue(tag);
    }
    
    @Override
    public List<Snippet> findTrendingSnippets() {
        // Get trending snippets from the last 30 days
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        return jpaSnippetRepository.findTrendingSnippets(since, PageRequest.of(0, 10));
    }
    
    @Override
    public List<Snippet> findTopSnippetsByLikes() {
        return jpaSnippetRepository.findTop10ByIsPublicTrueOrderByLikesDesc();
    }
    
    @Override
    public List<Snippet> findTopSnippetsByViews() {
        return jpaSnippetRepository.findTop10ByIsPublicTrueOrderByViewsDesc();
    }
    
    @Override
    public List<Snippet> findRecentPublicSnippets() {
        return jpaSnippetRepository.findTop10ByIsPublicTrueOrderByCreatedAtDesc();
    }
    
    @Override
    public List<String> findDistinctLanguages() {
        return jpaSnippetRepository.findDistinctLanguages();
    }
    
    @Override
    public void deleteById(Long id) {
        jpaSnippetRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaSnippetRepository.existsById(id);
    }
    
    @Override
    public boolean existsByIdAndUserId(Long snippetId, Long userId) {
        return jpaSnippetRepository.existsByIdAndUserId(snippetId, userId);
    }
    
    @Override
    public long count() {
        return jpaSnippetRepository.count();
    }
    
    @Override
    public long countByUser(User user) {
        return jpaSnippetRepository.countByUser(user);
    }
    
    @Override
    public long countByIsPublicTrue() {
        return jpaSnippetRepository.countByIsPublicTrue();
    }
}
