package com.codehaven.backend.application.usecase;

import com.codehaven.backend.application.dto.user.ChangePasswordRequest;
import com.codehaven.backend.application.dto.user.UpdateUserRequest;
import com.codehaven.backend.application.dto.user.UserProfileDto;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.service.UserService;
import com.codehaven.backend.infrastructure.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.codehaven.backend.domain.repository.ProjectRepository;
import com.codehaven.backend.domain.repository.SnippetRepository;
import com.codehaven.backend.domain.repository.BlogRepository;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementUseCase {
    
    private final UserService userService;
    private final UserServiceImpl userServiceImpl; // For additional methods like changePassword
    private final ProjectRepository projectRepository;
    private final SnippetRepository snippetRepository;
    private final BlogRepository blogRepository;
    
    public UserProfileDto getUserProfile(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        return mapToUserProfileDto(user);
    }
    
    public UserProfileDto updateUserProfile(Long userId, UpdateUserRequest request) {
        User userUpdate = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl())
                .githubUsername(request.getGithubUsername())
                .build();
        
        User updatedUser = userService.updateUser(userId, userUpdate);
        return mapToUserProfileDto(updatedUser);
    }
    
    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }
        
        userServiceImpl.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
    }
    
    public void deactivateUser(Long userId) {
        userService.deactivateUser(userId);
    }
    
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }
    
    public Page<UserProfileDto> getAllUsers(Pageable pageable) {
        return userService.findAllUsers(pageable)
                .map(this::mapToUserProfileDto);
    }
    
    public Page<UserProfileDto> getActiveUsers(Pageable pageable) {
        return userService.findActiveUsers(pageable)
                .map(this::mapToUserProfileDto);
    }
    
    public List<UserProfileDto> searchUsers(String username) {
        return userService.searchUsersByUsername(username)
                .stream()
                .map(this::mapToUserProfileDto)
                .collect(Collectors.toList());
    }
    
    public List<UserProfileDto> getTopContributors() {
        return userService.getTopContributors()
                .stream()
                .map(this::mapToUserProfileDto)
                .collect(Collectors.toList());
    }
    
    public UserProfileDto updateUserRole(Long userId, User.Role role) {
        User updatedUser = userService.updateUserRole(userId, role);
        return mapToUserProfileDto(updatedUser);
    }
    
    public long getTotalUserCount() {
        return userService.getTotalUserCount();
    }
    
    public Map<String, Object> getUserStats(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        Map<String, Object> stats = new HashMap<>();
        
        // Count user's content
        long projectCount = projectRepository.countByUser(user);
        long snippetCount = snippetRepository.countByUser(user);
        long blogCount = blogRepository.countByUser(user);
        
        stats.put("totalProjects", projectCount);
        stats.put("totalSnippets", snippetCount);
        stats.put("totalBlogs", blogCount);
        stats.put("totalViews", 0L); // TODO: Implement when view tracking is added
        stats.put("totalLikes", 0L); // TODO: Implement when like system is added
        stats.put("followersCount", 0L); // TODO: Implement when follow system is added
        stats.put("followingCount", 0L); // TODO: Implement when follow system is added
        
        return stats;
    }
    
    private UserProfileDto mapToUserProfileDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .githubUsername(user.getGithubUsername())
                .techStack(user.getTechStack())
                .role(user.getRole().name())
                .joinedAt(user.getJoinedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(user.getIsActive())
                .build();
    }
}
