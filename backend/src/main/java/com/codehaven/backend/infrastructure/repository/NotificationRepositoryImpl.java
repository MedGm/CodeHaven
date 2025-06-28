package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.Notification;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {
    
    private final JpaNotificationRepository jpaNotificationRepository;
    
    @Override
    public Notification save(Notification notification) {
        return jpaNotificationRepository.save(notification);
    }
    
    @Override
    public Optional<Notification> findById(Long id) {
        return jpaNotificationRepository.findById(id);
    }
    
    @Override
    public Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
        return jpaNotificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    public List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user) {
        return jpaNotificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }
    
    @Override
    public long countByUserAndIsReadFalse(User user) {
        return jpaNotificationRepository.countByUserAndIsReadFalse(user);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaNotificationRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void markAllAsReadByUser(Long userId) {
        jpaNotificationRepository.markAllAsReadByUser(userId);
    }
}
