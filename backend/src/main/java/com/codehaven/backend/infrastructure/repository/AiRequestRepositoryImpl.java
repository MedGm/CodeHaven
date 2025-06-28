package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.AiRequest;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.AiRequestRepository;
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
public class AiRequestRepositoryImpl implements AiRequestRepository {
    
    private final JpaAiRequestRepository jpaAiRequestRepository;
    
    @Override
    public AiRequest save(AiRequest aiRequest) {
        return jpaAiRequestRepository.save(aiRequest);
    }
    
    @Override
    public Optional<AiRequest> findById(Long id) {
        return jpaAiRequestRepository.findById(id);
    }
    
    @Override
    public Page<AiRequest> findAllByUser(User user, Pageable pageable) {
        return jpaAiRequestRepository.findAllByUser(user, pageable);
    }
    
    @Override
    public Page<AiRequest> findAllByType(AiRequest.RequestType type, Pageable pageable) {
        return jpaAiRequestRepository.findAllByType(type, pageable);
    }
    
    @Override
    public Page<AiRequest> findAllByStatus(AiRequest.Status status, Pageable pageable) {
        return jpaAiRequestRepository.findAllByStatus(status, pageable);
    }
    
    @Override
    public List<AiRequest> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end) {
        return jpaAiRequestRepository.findByUserAndCreatedAtBetween(user, start, end);
    }
    
    @Override
    public long countByUser(User user) {
        return jpaAiRequestRepository.countByUser(user);
    }
    
    @Override
    public long countByUserAndCreatedAtAfter(User user, LocalDateTime after) {
        return jpaAiRequestRepository.countByUserAndCreatedAtAfter(user, after);
    }
    
    @Override
    public long countByStatus(AiRequest.Status status) {
        return jpaAiRequestRepository.countByStatus(status);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaAiRequestRepository.deleteById(id);
    }
    
    @Override
    public List<AiRequest> findPendingRequests() {
        return jpaAiRequestRepository.findPendingRequests();
    }
    
    @Override
    public List<AiRequest> findRecentRequestsByUser(User user, int limit) {
        return jpaAiRequestRepository.findRecentRequestsByUser(user, PageRequest.of(0, limit));
    }
    
    @Override
    public List<AiRequest> findByUserOrderByCreatedAtDesc(User user) {
        return jpaAiRequestRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
