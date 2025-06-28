package com.codehaven.backend.domain.service;

import com.codehaven.backend.domain.model.Notification;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    Notification createNotification(User user, String title, String message, 
                                  Notification.NotificationType type, String actionUrl, String actionText);
    
    Page<Notification> getUserNotifications(User user, Pageable pageable);
    List<Notification> getUnreadNotifications(User user);
    long getUnreadCount(User user);
    
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long notificationId, Long userId);
    
    // Helper methods for creating specific types of notifications
    void notifyProjectCreated(User user, String projectName);
    void notifyBlogPublished(User user, String blogTitle);
    void notifySnippetShared(User user, String snippetTitle);
    void notifyWelcome(User user);
}
