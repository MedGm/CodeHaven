package com.codehaven.backend.application.dto.mapper;

import com.codehaven.backend.application.dto.user.UserProfileDto;
import com.codehaven.backend.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserProfileDto toUserProfileDto(User user) {
        if (user == null) {
            return null;
        }
        
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .githubUsername(user.getGithubUsername())
                .techStack(user.getTechStack())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .joinedAt(user.getJoinedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(user.getIsActive())
                .build();
    }
    
    public User fromUpdateRequest(com.codehaven.backend.application.dto.user.UpdateUserRequest request) {
        if (request == null) {
            return null;
        }
        
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl())
                .githubUsername(request.getGithubUsername())
                .build();
    }
}
