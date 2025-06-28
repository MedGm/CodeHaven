package com.codehaven.backend.application.dto.mapper;

import com.codehaven.backend.application.dto.snippet.CreateSnippetRequest;
import com.codehaven.backend.application.dto.snippet.SnippetDto;
import com.codehaven.backend.application.dto.snippet.UpdateSnippetRequest;
import com.codehaven.backend.domain.model.Snippet;
import com.codehaven.backend.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SnippetMapper {
    
    public Snippet toEntity(CreateSnippetRequest request, User author) {
        return Snippet.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .code(request.getCode())
                .language(request.getLanguage())
                .tags(request.getTags())
                .isPublic(request.getIsPublic())
                .isGist(request.getIsGist() != null ? request.getIsGist() : false)
                .gistUrl(request.getGistUrl())
                .user(author)
                .likes(0L)
                .views(0L)
                .build();
    }
    
    public void updateEntity(Snippet snippet, UpdateSnippetRequest request) {
        snippet.setTitle(request.getTitle());
        snippet.setDescription(request.getDescription());
        snippet.setCode(request.getCode());
        snippet.setLanguage(request.getLanguage());
        snippet.setTags(request.getTags());
        snippet.setIsPublic(request.getIsPublic());
        snippet.setIsGist(request.getIsGist() != null ? request.getIsGist() : false);
        snippet.setGistUrl(request.getGistUrl());
    }
    
    public SnippetDto toDto(Snippet snippet) {
        return toDto(snippet, null, false);
    }
    
    public SnippetDto toDto(Snippet snippet, Set<Long> likedSnippetIds, boolean includePrivateInfo) {
        boolean isLikedByCurrentUser = likedSnippetIds != null && likedSnippetIds.contains(snippet.getId());
        
        return SnippetDto.builder()
                .id(snippet.getId())
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .code(snippet.getCode())
                .language(snippet.getLanguage())
                .tags(snippet.getTags())
                .isPublic(snippet.getIsPublic())
                .isGist(snippet.getIsGist())
                .gistUrl(snippet.getGistUrl())
                .likesCount(snippet.getLikes().intValue())
                .viewsCount(snippet.getViews().intValue())
                .isLikedByCurrentUser(isLikedByCurrentUser)
                .authorUsername(snippet.getUser().getUsername())
                .authorName(snippet.getUser().getUsername()) // Using username as display name
                .createdAt(snippet.getCreatedAt())
                .updatedAt(snippet.getUpdatedAt())
                .build();
    }
    
    public List<SnippetDto> toDtoList(List<Snippet> snippets) {
        return snippets.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<SnippetDto> toDtoList(List<Snippet> snippets, Set<Long> likedSnippetIds) {
        return snippets.stream()
                .map(snippet -> toDto(snippet, likedSnippetIds, false))
                .collect(Collectors.toList());
    }
}
