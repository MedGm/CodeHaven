package com.codehaven.backend.controller;

import com.codehaven.backend.application.dto.auth.ApiResponse;
import com.codehaven.backend.application.dto.notification.CreateNotificationRequest;
import com.codehaven.backend.application.dto.notification.NotificationDto;
import com.codehaven.backend.application.dto.notification.NotificationResponse;
import com.codehaven.backend.domain.model.Notification;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.UserRepository;
import com.codehaven.backend.domain.service.NotificationService;
import com.codehaven.backend.security.CurrentUser;
import com.codehaven.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<NotificationResponse> getUserNotifications(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if user has any notifications, if not create some sample ones
        Page<Notification> notificationsPage = notificationService.getUserNotifications(user, PageRequest.of(0, 1));
        
        if (notificationsPage.getTotalElements() == 0) {
            log.info("Creating sample notifications for user: {}", user.getUsername());
            createSampleNotifications(user);
        }
        
        Pageable pageable = PageRequest.of(page, size);
        notificationsPage = notificationService.getUserNotifications(user, pageable);
        
        List<NotificationDto> notificationDtos = notificationsPage.getContent()
                .stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
        
        long unreadCount = notificationService.getUnreadCount(user);
        
        NotificationResponse response = NotificationResponse.builder()
                .notifications(notificationDtos)
                .unreadCount(unreadCount)
                .total(notificationsPage.getTotalElements())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        long count = notificationService.getUnreadCount(user);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse> markAsRead(
            @PathVariable Long notificationId,
            @CurrentUser UserPrincipal userPrincipal) {
        
        notificationService.markAsRead(notificationId, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Notification marked as read"));
    }
    
    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponse> markAllAsRead(@CurrentUser UserPrincipal userPrincipal) {
        notificationService.markAllAsRead(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "All notifications marked as read"));
    }
    
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse> deleteNotification(
            @PathVariable Long notificationId,
            @CurrentUser UserPrincipal userPrincipal) {
        
        notificationService.deleteNotification(notificationId, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Notification deleted"));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationDto> createNotification(
            @Valid @RequestBody CreateNotificationRequest request,
            @RequestParam Long userId) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Notification notification = notificationService.createNotification(
                user, request.getTitle(), request.getMessage(), 
                request.getType(), request.getActionUrl(), request.getActionText());
        
        return ResponseEntity.ok(NotificationDto.fromEntity(notification));
    }
    
    private void createSampleNotifications(User user) {
        // Create welcome notification
        notificationService.createNotification(
                user, 
                "Welcome to CodeHaven!", 
                "Start by creating your first project or exploring AI-powered features.",
                Notification.NotificationType.INFO,
                "/projects/create",
                "Create Project"
        );
        
        // Create feature notification
        notificationService.createNotification(
                user, 
                "New AI Feature Available", 
                "Try our enhanced code review feature with improved accuracy.",
                Notification.NotificationType.SUCCESS,
                "/ai/code-review",
                "Try It"
        );
        
        // Create tips notification
        notificationService.createNotification(
                user, 
                "Pro Tip: Code Snippets", 
                "Share your JavaScript utility functions with the community.",
                Notification.NotificationType.INFO,
                "/snippets",
                "View Snippets"
        );
    }
}
