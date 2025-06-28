package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.AiRequest;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaAiRequestRepository extends JpaRepository<AiRequest, Long> {
    
    Page<AiRequest> findAllByUser(User user, Pageable pageable);
    
    Page<AiRequest> findAllByType(AiRequest.RequestType type, Pageable pageable);
    
    Page<AiRequest> findAllByStatus(AiRequest.Status status, Pageable pageable);
    
    List<AiRequest> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
    
    long countByUser(User user);
    
    long countByUserAndCreatedAtAfter(User user, LocalDateTime after);
    
    long countByStatus(AiRequest.Status status);
    
    @Query("SELECT a FROM AiRequest a WHERE a.status = 'PENDING' ORDER BY a.createdAt ASC")
    List<AiRequest> findPendingRequests();
    
    @Query("SELECT a FROM AiRequest a WHERE a.user = :user ORDER BY a.createdAt DESC")
    List<AiRequest> findRecentRequestsByUser(@Param("user") User user, Pageable pageable);
    
    List<AiRequest> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT a FROM AiRequest a WHERE a.status = 'FAILED' ORDER BY a.createdAt DESC")
    List<AiRequest> findFailedRequests();
}
