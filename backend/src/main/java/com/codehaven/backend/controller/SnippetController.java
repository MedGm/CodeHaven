package com.codehaven.backend.controller;

import com.codehaven.backend.application.dto.snippet.CreateSnippetRequest;
import com.codehaven.backend.application.dto.snippet.SnippetDto;
import com.codehaven.backend.application.dto.snippet.UpdateSnippetRequest;
import com.codehaven.backend.application.usecase.SnippetManagementUseCase;
import com.codehaven.backend.security.CurrentUser;
import com.codehaven.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/snippets")
@RequiredArgsConstructor
@Slf4j
public class SnippetController {

    private final SnippetManagementUseCase snippetManagementUseCase;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SnippetDto> createSnippet(
            @Valid @RequestBody CreateSnippetRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Creating snippet for user: {}", currentUser.getUsername());
        
        SnippetDto snippet = snippetManagementUseCase.createSnippet(request, currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(snippet);
    }

    @PutMapping("/{snippetId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SnippetDto> updateSnippet(
            @PathVariable Long snippetId,
            @Valid @RequestBody UpdateSnippetRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Updating snippet {} for user: {}", snippetId, currentUser.getUsername());
        
        SnippetDto snippet = snippetManagementUseCase.updateSnippet(snippetId, request, currentUser.getUsername());
        return ResponseEntity.ok(snippet);
    }

    @GetMapping("/{snippetId}")
    public ResponseEntity<SnippetDto> getSnippetById(
            @PathVariable Long snippetId) {
        
        log.info("Fetching snippet {}", snippetId);
        
        SnippetDto snippet = snippetManagementUseCase.getSnippetById(snippetId, null);
        return ResponseEntity.ok(snippet);
    }

    @DeleteMapping("/{snippetId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteSnippet(
            @PathVariable Long snippetId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Deleting snippet {} for user: {}", snippetId, currentUser.getUsername());
        
        snippetManagementUseCase.deleteSnippet(snippetId, currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public")
    public ResponseEntity<Page<SnippetDto>> getPublicSnippets(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.info("Fetching public snippets, page: {}", pageable.getPageNumber());
        
        Page<SnippetDto> snippets = snippetManagementUseCase.getPublicSnippets(pageable);
        return ResponseEntity.ok(snippets);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<Page<SnippetDto>> getUserSnippets(
            @PathVariable String username,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.info("Fetching snippets for user: {}", username);
        
        Page<SnippetDto> snippets = snippetManagementUseCase.getUserSnippets(username, pageable, null);
        return ResponseEntity.ok(snippets);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SnippetDto>> searchSnippetsByTitle(
            @RequestParam String title) {
        
        log.info("Searching snippets with title: {}", title);
        
        List<SnippetDto> snippets = snippetManagementUseCase.searchSnippetsByTitle(title);
        return ResponseEntity.ok(snippets);
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<SnippetDto>> getSnippetsByLanguage(
            @PathVariable String language) {
        
        log.info("Fetching snippets by language: {}", language);
        
        List<SnippetDto> snippets = snippetManagementUseCase.getSnippetsByLanguage(language);
        return ResponseEntity.ok(snippets);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<SnippetDto>> getSnippetsByTag(
            @PathVariable String tag) {
        
        log.info("Fetching snippets by tag: {}", tag);
        
        List<SnippetDto> snippets = snippetManagementUseCase.getSnippetsByTag(tag);
        return ResponseEntity.ok(snippets);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<SnippetDto>> getTrendingSnippets() {
        
        log.info("Fetching trending snippets");
        
        List<SnippetDto> snippets = snippetManagementUseCase.getTrendingSnippets();
        return ResponseEntity.ok(snippets);
    }

    @PostMapping("/{snippetId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SnippetDto> likeSnippet(
            @PathVariable Long snippetId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("User {} liking snippet {}", currentUser.getUsername(), snippetId);
        
        SnippetDto snippet = snippetManagementUseCase.likeSnippet(snippetId, currentUser.getUsername());
        return ResponseEntity.ok(snippet);
    }

    @DeleteMapping("/{snippetId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SnippetDto> unlikeSnippet(
            @PathVariable Long snippetId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("User {} unliking snippet {}", currentUser.getUsername(), snippetId);
        
        SnippetDto snippet = snippetManagementUseCase.unlikeSnippet(snippetId, currentUser.getUsername());
        return ResponseEntity.ok(snippet);
    }

    @GetMapping("/languages")
    public ResponseEntity<List<String>> getAllLanguages() {
        
        log.info("Fetching all languages");
        
        List<String> languages = snippetManagementUseCase.getAllLanguages();
        return ResponseEntity.ok(languages);
    }
}
