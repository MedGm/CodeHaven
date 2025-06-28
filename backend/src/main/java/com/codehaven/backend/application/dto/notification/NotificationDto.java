package com.codehaven.backend.application.dto.notification;

import com.codehaven.backend.domain.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private String actionUrl;
    private String actionText;
    
    public static NotificationDto fromEntity(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType().name().toLowerCase())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .actionUrl(notification.getActionUrl())
                .actionText(notification.getActionText())
                .build();
    }
}
