package com.codehaven.backend.domain.service;

import com.codehaven.backend.domain.model.AiRequest;
import com.codehaven.backend.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AiService {
    
    CompletableFuture<AiRequest> requestCodeReview(User user, String code, String language);
    
    CompletableFuture<AiRequest> requestBugFix(User user, String code, String language, String description);
    
    CompletableFuture<AiRequest> requestCodeOptimization(User user, String code, String language);
    
    CompletableFuture<AiRequest> requestCodeExplanation(User user, String code, String language);
    
    CompletableFuture<AiRequest> requestDocumentation(User user, String code, String language);
    
    CompletableFuture<AiRequest> requestTagSuggestion(User user, String title, String description);
    
    CompletableFuture<AiRequest> requestUnitTestGeneration(User user, String code, String language);
    
    Optional<AiRequest> findRequestById(Long requestId);
    
    List<AiRequest> getUserRequests(User user, int limit);
    
    boolean hasUserExceededDailyLimit(User user);
    
    long getUserRequestCountToday(User user);
    
    void processRequest(AiRequest request);
    
    void processPendingRequests();
}
