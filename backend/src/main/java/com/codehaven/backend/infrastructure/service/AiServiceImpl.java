package com.codehaven.backend.infrastructure.service;

import com.codehaven.backend.domain.model.AiRequest;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.AiRequestRepository;
import com.codehaven.backend.domain.service.AiService;
import com.codehaven.backend.infrastructure.client.GroqClient;
import com.codehaven.backend.infrastructure.client.GroqResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {
    
    private final AiRequestRepository aiRequestRepository;
    private final GroqClient groqClient;
    
    private static final int DAILY_REQUEST_LIMIT = 50; // Configurable daily limit per user
    
    @Override
    @Async
    @Transactional
    public CompletableFuture<AiRequest> requestCodeReview(User user, String code, String language) {
        if (hasUserExceededDailyLimit(user)) {
            throw new RuntimeException("Daily request limit exceeded");
        }
        
        AiRequest aiRequest = AiRequest.builder()
                .user(user)
                .type(AiRequest.RequestType.CODE_REVIEW)
                .inputData(formatCodeInput(code, language))
                .status(AiRequest.Status.PENDING)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        return processAsyncRequest(aiRequest, () -> groqClient.reviewCode(code, language));
    }
    
    @Override
    @Async
    @Transactional
    public CompletableFuture<AiRequest> requestBugFix(User user, String code, String language, String description) {
        if (hasUserExceededDailyLimit(user)) {
            throw new RuntimeException("Daily request limit exceeded");
        }
        
        AiRequest aiRequest = AiRequest.builder()
                .user(user)
                .type(AiRequest.RequestType.BUG_FIX)
                .inputData(formatBugFixInput(code, language, description))
                .status(AiRequest.Status.PENDING)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        return processAsyncRequest(aiRequest, () -> groqClient.suggestBugFix(code, language, description));
    }
    
    @Override
    @Async
    @Transactional
    public CompletableFuture<AiRequest> requestCodeOptimization(User user, String code, String language) {
        if (hasUserExceededDailyLimit(user)) {
            throw new RuntimeException("Daily request limit exceeded");
        }
        
        AiRequest aiRequest = AiRequest.builder()
                .user(user)
                .type(AiRequest.RequestType.CODE_OPTIMIZATION)
                .inputData(formatCodeInput(code, language))
                .status(AiRequest.Status.PENDING)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        return processAsyncRequest(aiRequest, () -> groqClient.optimizeCode(code, language));
    }
    
    @Override
    @Async
    @Transactional
    public CompletableFuture<AiRequest> requestCodeExplanation(User user, String code, String language) {
        if (hasUserExceededDailyLimit(user)) {
            throw new RuntimeException("Daily request limit exceeded");
        }
        
        AiRequest aiRequest = AiRequest.builder()
                .user(user)
                .type(AiRequest.RequestType.EXPLANATION)
                .inputData(formatCodeInput(code, language))
                .status(AiRequest.Status.PENDING)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        return processAsyncRequest(aiRequest, () -> groqClient.explainCode(code, language));
    }
    
    @Override
    @Async
    @Transactional
    public CompletableFuture<AiRequest> requestDocumentation(User user, String code, String language) {
        if (hasUserExceededDailyLimit(user)) {
            throw new RuntimeException("Daily request limit exceeded");
        }
        
        AiRequest aiRequest = AiRequest.builder()
                .user(user)
                .type(AiRequest.RequestType.DOCUMENTATION)
                .inputData(formatCodeInput(code, language))
                .status(AiRequest.Status.PENDING)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        return processAsyncRequest(aiRequest, () -> groqClient.generateDocumentation(code, language));
    }
    
    @Override
    @Async
    @Transactional
    public CompletableFuture<AiRequest> requestTagSuggestion(User user, String title, String description) {
        if (hasUserExceededDailyLimit(user)) {
            throw new RuntimeException("Daily request limit exceeded");
        }
        
        AiRequest aiRequest = AiRequest.builder()
                .user(user)
                .type(AiRequest.RequestType.TAG_SUGGESTION)
                .inputData(formatTagInput(title, description))
                .status(AiRequest.Status.PENDING)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        return processAsyncRequest(aiRequest, () -> groqClient.suggestTags(title, description));
    }
    
    @Override
    @Async
    @Transactional
    public CompletableFuture<AiRequest> requestUnitTestGeneration(User user, String code, String language) {
        if (hasUserExceededDailyLimit(user)) {
            throw new RuntimeException("Daily request limit exceeded");
        }
        
        AiRequest aiRequest = AiRequest.builder()
                .user(user)
                .type(AiRequest.RequestType.UNIT_TEST_GENERATION)
                .inputData(formatCodeInput(code, language))
                .status(AiRequest.Status.PENDING)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        return processAsyncRequest(aiRequest, () -> groqClient.generateUnitTests(code, language));
    }
    
    @Override
    public Optional<AiRequest> findRequestById(Long requestId) {
        return aiRequestRepository.findById(requestId);
    }
    
    @Override
    public List<AiRequest> getUserRequests(User user, int limit) {
        return aiRequestRepository.findRecentRequestsByUser(user, limit);
    }
    
    @Override
    public boolean hasUserExceededDailyLimit(User user) {
        return getUserRequestCountToday(user) >= DAILY_REQUEST_LIMIT;
    }
    
    @Override
    public long getUserRequestCountToday(User user) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        return aiRequestRepository.countByUserAndCreatedAtAfter(user, startOfDay);
    }
    
    @Override
    @Transactional
    public void processRequest(AiRequest request) {
        // This method can be used for manual processing or retry logic
        // Implementation would depend on specific requirements
        log.info("Processing AI request: {}", request.getId());
    }
    
    @Override
    public void processPendingRequests() {
        List<AiRequest> pendingRequests = aiRequestRepository.findPendingRequests();
        log.info("Found {} pending AI requests to process", pendingRequests.size());
        
        pendingRequests.forEach(request -> {
            try {
                processRequest(request);
            } catch (Exception e) {
                log.error("Error processing AI request {}: {}", request.getId(), e.getMessage());
                updateRequestStatus(request, AiRequest.Status.FAILED, e.getMessage());
            }
        });
    }
    
    private CompletableFuture<AiRequest> processAsyncRequest(
            AiRequest aiRequest, 
            java.util.function.Supplier<CompletableFuture<GroqResponse>> groqCall) {
        
        long startTime = System.currentTimeMillis();
        
        return groqCall.get()
                .thenApply(response -> {
                    long processingTime = System.currentTimeMillis() - startTime;
                    
                    String responseContent = extractResponseContent(response);
                    
                    aiRequest.setResponseData(responseContent);
                    aiRequest.setStatus(AiRequest.Status.COMPLETED);
                    aiRequest.setProcessingTimeMs(processingTime);
                    
                    // Extract token usage if available
                    if (response.getUsage() != null) {
                        aiRequest.setPromptTokens(response.getUsage().getPromptTokens());
                        aiRequest.setCompletionTokens(response.getUsage().getCompletionTokens());
                        aiRequest.setTotalTokens(response.getUsage().getTotalTokens());
                    }
                    
                    return aiRequestRepository.save(aiRequest);
                })
                .exceptionally(throwable -> {
                    long processingTime = System.currentTimeMillis() - startTime;
                    
                    log.error("Error processing AI request {}: {}", aiRequest.getId(), throwable.getMessage());
                    
                    aiRequest.setStatus(AiRequest.Status.FAILED);
                    aiRequest.setErrorMessage(throwable.getMessage());
                    aiRequest.setProcessingTimeMs(processingTime);
                    
                    return aiRequestRepository.save(aiRequest);
                });
    }
    
    private String extractResponseContent(GroqResponse response) {
        if (response.getChoices() != null && !response.getChoices().isEmpty()) {
            GroqResponse.Choice firstChoice = response.getChoices().get(0);
            if (firstChoice.getMessage() != null) {
                return firstChoice.getMessage().getContent();
            }
        }
        return "No response content available";
    }
    
    private void updateRequestStatus(AiRequest request, AiRequest.Status status, String errorMessage) {
        request.setStatus(status);
        if (errorMessage != null) {
            request.setErrorMessage(errorMessage);
        }
        aiRequestRepository.save(request);
    }
    
    private String formatCodeInput(String code, String language) {
        return String.format("Language: %s\nCode:\n%s", language, code);
    }
    
    private String formatBugFixInput(String code, String language, String description) {
        return String.format("Language: %s\nDescription: %s\nCode:\n%s", language, description, code);
    }
    
    private String formatTagInput(String title, String description) {
        return String.format("Title: %s\nDescription: %s", title, description);
    }
}
