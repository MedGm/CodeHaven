package com.codehaven.backend.application.usecase;

import com.codehaven.backend.application.dto.mapper.SnippetMapper;
import com.codehaven.backend.application.dto.snippet.CreateSnippetRequest;
import com.codehaven.backend.application.dto.snippet.SnippetDto;
import com.codehaven.backend.application.dto.snippet.UpdateSnippetRequest;
import com.codehaven.backend.domain.model.Snippet;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.service.SnippetService;
import com.codehaven.backend.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SnippetManagementUseCase {

    private final SnippetService snippetService;
    private final UserService userService;
    private final SnippetMapper snippetMapper;

    public SnippetDto createSnippet(CreateSnippetRequest request, String username) {
        log.info("Creating snippet for user: {}", username);
        
        User author = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        Snippet snippet = snippetMapper.toEntity(request, author);
        Snippet savedSnippet = snippetService.createSnippet(snippet);
        
        log.info("Snippet created with ID: {}", savedSnippet.getId());
        return snippetMapper.toDto(savedSnippet);
    }

    public SnippetDto updateSnippet(Long snippetId, UpdateSnippetRequest request, String username) {
        log.info("Updating snippet {} for user: {}", snippetId, username);
        
        Snippet snippet = snippetService.findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found: " + snippetId));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Check if user owns the snippet
        if (!snippet.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own snippets");
        }
        
        snippetMapper.updateEntity(snippet, request);
        Snippet updatedSnippet = snippetService.updateSnippet(snippetId, snippet);
        
        log.info("Snippet {} updated successfully", snippetId);
        return snippetMapper.toDto(updatedSnippet);
    }

    @Transactional(readOnly = true)
    public SnippetDto getSnippetById(Long snippetId, String currentUsername) {
        log.info("Fetching snippet: {}", snippetId);
        
        Snippet snippet = snippetService.findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found: " + snippetId));
        
        // Check if snippet is public or user owns it
        if (!snippet.getIsPublic() && (currentUsername == null || 
            !snippet.getUser().getUsername().equals(currentUsername))) {
            throw new AccessDeniedException("This snippet is private");
        }
        
        // Increment view count if viewing others' snippets
        if (currentUsername == null || !snippet.getUser().getUsername().equals(currentUsername)) {
            snippetService.incrementViews(snippetId);
        }
        
        return snippetMapper.toDto(snippet);
    }

    public void deleteSnippet(Long snippetId, String username) {
        log.info("Deleting snippet {} for user: {}", snippetId, username);
        
        Snippet snippet = snippetService.findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found: " + snippetId));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Check if user owns the snippet
        if (!snippet.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only delete your own snippets");
        }
        
        snippetService.deleteSnippet(snippetId);
        log.info("Snippet {} deleted successfully", snippetId);
    }

    @Transactional(readOnly = true)
    public Page<SnippetDto> getPublicSnippets(Pageable pageable) {
        log.info("Fetching public snippets, page: {}", pageable.getPageNumber());
        
        Page<Snippet> snippets = snippetService.findPublicSnippets(pageable);
        List<SnippetDto> snippetDtos = snippetMapper.toDtoList(snippets.getContent());
        return new PageImpl<>(snippetDtos, pageable, snippets.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<SnippetDto> getUserSnippets(String targetUsername, Pageable pageable, String currentUsername) {
        log.info("Fetching snippets for user: {}", targetUsername);
        
        User targetUser = userService.findByUsername(targetUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + targetUsername));
        boolean isOwnProfile = currentUsername != null && currentUsername.equals(targetUsername);
        
        Page<Snippet> snippets;
        if (isOwnProfile) {
            // Show all snippets for own profile
            snippets = snippetService.findSnippetsByUser(targetUser, pageable);
        } else {
            // Show only public snippets for other profiles
            snippets = snippetService.findPublicSnippetsByUser(targetUser, pageable);
        }
        
        List<SnippetDto> snippetDtos = snippetMapper.toDtoList(snippets.getContent());
        return new PageImpl<>(snippetDtos, pageable, snippets.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<SnippetDto> searchSnippetsByTitle(String title) {
        log.info("Searching snippets with title: {}", title);
        
        List<Snippet> snippets = snippetService.searchSnippetsByTitle(title);
        return snippetMapper.toDtoList(snippets);
    }

    @Transactional(readOnly = true)
    public List<SnippetDto> getSnippetsByLanguage(String language) {
        log.info("Fetching snippets by language: {}", language);
        
        List<Snippet> snippets = snippetService.findSnippetsByLanguage(language);
        return snippetMapper.toDtoList(snippets);
    }

    @Transactional(readOnly = true)
    public List<SnippetDto> getSnippetsByTag(String tag) {
        log.info("Fetching snippets by tag: {}", tag);
        
        List<Snippet> snippets = snippetService.findSnippetsByTag(tag);
        return snippetMapper.toDtoList(snippets);
    }

    @Transactional(readOnly = true)
    public List<SnippetDto> getTrendingSnippets() {
        log.info("Fetching trending snippets");
        
        List<Snippet> snippets = snippetService.getTrendingSnippets();
        return snippetMapper.toDtoList(snippets);
    }

    public SnippetDto likeSnippet(Long snippetId, String username) {
        log.info("User {} liking snippet {}", username, snippetId);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        Snippet snippet = snippetService.findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found: " + snippetId));
        
        // Check if snippet is public or user owns it
        if (!snippet.getIsPublic() && !snippet.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot like a private snippet");
        }
        
        Snippet updatedSnippet = snippetService.likeSnippet(snippetId);
        log.info("Snippet {} liked by user {}", snippetId, username);
        return snippetMapper.toDto(updatedSnippet);
    }

    public SnippetDto unlikeSnippet(Long snippetId, String username) {
        log.info("User {} unliking snippet {}", username, snippetId);
        
        // Verify user exists
        userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Snippet updatedSnippet = snippetService.unlikeSnippet(snippetId);
        log.info("Snippet {} unliked by user {}", snippetId, username);
        return snippetMapper.toDto(updatedSnippet);
    }

    @Transactional(readOnly = true)
    public List<String> getAllLanguages() {
        return snippetService.getAllLanguages();
    }
}
