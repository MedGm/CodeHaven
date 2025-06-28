package com.codehaven.backend.controller;

import com.codehaven.backend.application.dto.auth.ApiResponse;
import com.codehaven.backend.application.dto.user.ChangePasswordRequest;
import com.codehaven.backend.application.dto.user.UpdateUserRequest;
import com.codehaven.backend.application.dto.user.UserProfileDto;
import com.codehaven.backend.application.usecase.UserManagementUseCase;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.security.CurrentUser;
import com.codehaven.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserManagementUseCase userManagementUseCase;
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        UserProfileDto user = userManagementUseCase.getUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        UserProfileDto user = userManagementUseCase.getUserProfile(userId);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateCurrentUserProfile(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateUserRequest request) {
        UserProfileDto updatedUser = userManagementUseCase.updateUserProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserProfileDto updatedUser = userManagementUseCase.updateUserProfile(userId, request);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PostMapping("/me/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest request) {
        userManagementUseCase.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
    }
    
    @DeleteMapping("/me/deactivate")
    public ResponseEntity<ApiResponse> deactivateCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        userManagementUseCase.deactivateUser(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Account deactivated successfully"));
    }
    
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        userManagementUseCase.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
    }
    
    @PostMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Long userId) {
        userManagementUseCase.deactivateUser(userId);
        return ResponseEntity.ok(new ApiResponse(true, "User deactivated successfully"));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserProfileDto>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        Page<UserProfileDto> users = activeOnly 
            ? userManagementUseCase.getActiveUsers(pageable)
            : userManagementUseCase.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<UserProfileDto>> searchUsers(@RequestParam String username) {
        List<UserProfileDto> users = userManagementUseCase.searchUsers(username);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/top-contributors")
    public ResponseEntity<List<UserProfileDto>> getTopContributors() {
        List<UserProfileDto> contributors = userManagementUseCase.getTopContributors();
        return ResponseEntity.ok(contributors);
    }
    
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> updateUserRole(
            @PathVariable Long userId,
            @RequestParam User.Role role) {
        UserProfileDto updatedUser = userManagementUseCase.updateUserRole(userId, role);
        return ResponseEntity.ok(updatedUser);
    }
    
    @GetMapping("/me/stats")
    public ResponseEntity<Map<String, Object>> getCurrentUserStats(@CurrentUser UserPrincipal userPrincipal) {
        Map<String, Object> stats = userManagementUseCase.getUserStats(userPrincipal.getId());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable Long userId) {
        Map<String, Object> stats = userManagementUseCase.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/stats/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalUserCount() {
        long count = userManagementUseCase.getTotalUserCount();
        return ResponseEntity.ok(count);
    }
}
