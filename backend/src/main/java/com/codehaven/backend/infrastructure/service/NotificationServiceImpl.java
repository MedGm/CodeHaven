package com.codehaven.backend.infrastructure.service;

import com.codehaven.backend.domain.model.Notification;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.NotificationRepository;
import com.codehaven.backend.domain.service.CacheService;
import com.codehaven.backend.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final CacheService cacheService;
    
    private static final String UNREAD_COUNT_CACHE_KEY = "notification:unread:count:";
    private static final long CACHE_TTL_SECONDS = 300; // 5 minutes
    
    @Override
    public Notification createNotification(User user, String title, String message, 
                                         Notification.NotificationType type, String actionUrl, String actionText) {
        log.info("Creating notification for user {} with title: {}", user.getUsername(), title);
        
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .actionUrl(actionUrl)
                .actionText(actionText)
                .build();
        
        Notification saved = notificationRepository.save(notification);
        
        // Invalidate cache for unread count
        cacheService.delete(UNREAD_COUNT_CACHE_KEY + user.getId());
        
        log.info("Notification created with ID: {} for user: {}", saved.getId(), user.getUsername());
        return saved;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        String cacheKey = UNREAD_COUNT_CACHE_KEY + user.getId();
        
        return cacheService.get(cacheKey, Long.class)
                .orElseGet(() -> {
                    long count = notificationRepository.countByUserAndIsReadFalse(user);
                    cacheService.put(cacheKey, count, CACHE_TTL_SECONDS);
                    return count;
                });
    }
    
    @Override
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId)
                .ifPresentOrElse(notification -> {
                    if (!notification.getUser().getId().equals(userId)) {
                        throw new IllegalArgumentException("Notification does not belong to user");
                    }
                    
                    if (!notification.getIsRead()) {
                        notification.setIsRead(true);
                        notificationRepository.save(notification);
                        
                        // Invalidate cache
                        cacheService.delete(UNREAD_COUNT_CACHE_KEY + userId);
                        
                        log.info("Marked notification {} as read for user {}", notificationId, userId);
                    }
                }, () -> {
                    throw new IllegalArgumentException("Notification not found with ID: " + notificationId);
                });
    }
    
    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUser(userId);
        
        // Invalidate cache
        cacheService.delete(UNREAD_COUNT_CACHE_KEY + userId);
        
        log.info("Marked all notifications as read for user {}", userId);
    }
    
    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId)
                .ifPresentOrElse(notification -> {
                    if (!notification.getUser().getId().equals(userId)) {
                        throw new IllegalArgumentException("Notification does not belong to user");
                    }
                    
                    notificationRepository.deleteById(notificationId);
                    
                    // Invalidate cache
                    cacheService.delete(UNREAD_COUNT_CACHE_KEY + userId);
                    
                    log.info("Deleted notification {} for user {}", notificationId, userId);
                }, () -> {
                    throw new IllegalArgumentException("Notification not found with ID: " + notificationId);
                });
    }
    
    @Override
    public void notifyProjectCreated(User user, String projectName) {
        createNotification(user, "Project Created", 
                         "Your project '" + projectName + "' has been created successfully!", 
                         Notification.NotificationType.SUCCESS, 
                         "/projects", "View Projects");
    }
    
    @Override
    public void notifyBlogPublished(User user, String blogTitle) {
        createNotification(user, "Blog Published", 
                         "Your blog post '" + blogTitle + "' has been published!", 
                         Notification.NotificationType.SUCCESS, 
                         "/blogs", "View Blogs");
    }
    
    @Override
    public void notifySnippetShared(User user, String snippetTitle) {
        createNotification(user, "Snippet Shared", 
                         "Your code snippet '" + snippetTitle + "' has been shared!", 
                         Notification.NotificationType.INFO, 
                         "/snippets", "View Snippets");
    }
    
    @Override
    public void notifyWelcome(User user) {
        createNotification(user, "Welcome to CodeHaven!", 
                         "Welcome to CodeHaven! Start by creating your first project or exploring our AI-powered features.", 
                         Notification.NotificationType.INFO, 
                         "/dashboard", "Get Started");
    }
}
