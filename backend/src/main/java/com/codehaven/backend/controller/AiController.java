package com.codehaven.backend.controller;

import com.codehaven.backend.application.dto.ai.*;
import com.codehaven.backend.application.usecase.AiPoweredFeaturesUseCase;
import com.codehaven.backend.security.CurrentUser;
import com.codehaven.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final AiPoweredFeaturesUseCase aiPoweredFeaturesUseCase;

    @PostMapping("/review")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AiResponseDto> reviewCode(
            @Valid @RequestBody CodeReviewRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Code review request from user: {}", currentUser.getUsername());
        AiResponseDto response = aiPoweredFeaturesUseCase.reviewCode(request, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bug-fix")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AiResponseDto> fixBug(
            @Valid @RequestBody BugFixRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Bug fix request from user: {}", currentUser.getUsername());
        AiResponseDto response = aiPoweredFeaturesUseCase.fixBug(request, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/optimize")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AiResponseDto> optimizeCode(
            @Valid @RequestBody CodeOptimizationRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Code optimization request from user: {}", currentUser.getUsername());
        AiResponseDto response = aiPoweredFeaturesUseCase.optimizeCode(request, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/explain")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AiResponseDto> explainCode(
            @Valid @RequestBody CodeExplanationRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Code explanation request from user: {}", currentUser.getUsername());
        AiResponseDto response = aiPoweredFeaturesUseCase.explainCode(request, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AiResponseDto> generateCode(
            @Valid @RequestBody CodeGenerationRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Code generation request from user: {}", currentUser.getUsername());
        AiResponseDto response = aiPoweredFeaturesUseCase.generateCode(request, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<AiResponseDto>> getAiHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("AI history request from user: {} (page: {}, size: {})", 
                currentUser.getUsername(), page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AiResponseDto> history = aiPoweredFeaturesUseCase.getUserAiHistory(currentUser.getUsername(), pageable);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/request/{requestId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AiResponseDto> getAiRequest(
            @PathVariable Long requestId,
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("AI request details request from user: {} for request: {}", 
                currentUser.getUsername(), requestId);
        
        AiResponseDto response = aiPoweredFeaturesUseCase.getAiRequestById(requestId, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> testAiEndpoint(@CurrentUser UserPrincipal currentUser) {
        log.info("AI test endpoint called by user: {}", currentUser.getUsername());
        return ResponseEntity.ok("AI endpoint is working for user: " + currentUser.getUsername());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Invalid argument in AI request: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("Error processing AI request: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to process AI request: " + e.getMessage());
    }
}
