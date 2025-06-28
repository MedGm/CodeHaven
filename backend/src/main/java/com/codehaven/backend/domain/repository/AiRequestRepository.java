package com.codehaven.backend.domain.repository;

import com.codehaven.backend.domain.model.AiRequest;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AiRequestRepository {
    
    AiRequest save(AiRequest aiRequest);
    
    Optional<AiRequest> findById(Long id);
    
    Page<AiRequest> findAllByUser(User user, Pageable pageable);
    
    Page<AiRequest> findAllByType(AiRequest.RequestType type, Pageable pageable);
    
    Page<AiRequest> findAllByStatus(AiRequest.Status status, Pageable pageable);
    
    List<AiRequest> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
    
    long countByUser(User user);
    
    long countByUserAndCreatedAtAfter(User user, LocalDateTime after);
    
    long countByStatus(AiRequest.Status status);
    
    void deleteById(Long id);
    
    List<AiRequest> findPendingRequests();
    
    List<AiRequest> findRecentRequestsByUser(User user, int limit);
    
    List<AiRequest> findByUserOrderByCreatedAtDesc(User user);
}
