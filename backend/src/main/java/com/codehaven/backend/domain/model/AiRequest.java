package com.codehaven.backend.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType type;
    
    @Column(name = "input_data", columnDefinition = "TEXT", nullable = false)
    private String inputData;
    
    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData;
    
    @Column(name = "prompt_tokens")
    private Integer promptTokens;
    
    @Column(name = "completion_tokens")
    private Integer completionTokens;
    
    @Column(name = "total_tokens")
    private Integer totalTokens;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum RequestType {
        CODE_REVIEW, 
        BUG_FIX, 
        CODE_OPTIMIZATION, 
        EXPLANATION, 
        DOCUMENTATION, 
        TAG_SUGGESTION,
        UNIT_TEST_GENERATION,
        CODE_GENERATION
    }
    
    public enum Status {
        PENDING, 
        PROCESSING, 
        COMPLETED, 
        FAILED
    }
}
