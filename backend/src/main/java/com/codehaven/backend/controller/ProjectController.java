package com.codehaven.backend.controller;

import com.codehaven.backend.application.dto.auth.ApiResponse;
import com.codehaven.backend.application.dto.project.CreateProjectRequest;
import com.codehaven.backend.application.dto.project.ProjectDto;
import com.codehaven.backend.application.dto.project.UpdateProjectRequest;
import com.codehaven.backend.application.usecase.ProjectManagementUseCase;
import com.codehaven.backend.security.CurrentUser;
import com.codehaven.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectManagementUseCase projectManagementUseCase;
    
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @CurrentUser UserPrincipal userPrincipal) {
        ProjectDto project = projectManagementUseCase.createProject(request, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }
    
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long projectId) {
        ProjectDto project = projectManagementUseCase.getProjectAndIncrementViews(projectId);
        return ResponseEntity.ok(project);
    }
    
    @GetMapping("/{projectId}/details")
    public ResponseEntity<ProjectDto> getProjectDetails(@PathVariable Long projectId) {
        ProjectDto project = projectManagementUseCase.getProject(projectId);
        return ResponseEntity.ok(project);
    }
    
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequest request,
            @CurrentUser UserPrincipal userPrincipal) {
        ProjectDto updatedProject = projectManagementUseCase.updateProject(projectId, request, userPrincipal.getId());
        return ResponseEntity.ok(updatedProject);
    }
    
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse> deleteProject(
            @PathVariable Long projectId,
            @CurrentUser UserPrincipal userPrincipal) {
        projectManagementUseCase.deleteProject(projectId, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Project deleted successfully"));
    }
    
    @GetMapping
    public ResponseEntity<Page<ProjectDto>> getAllProjects(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProjectDto> projects = projectManagementUseCase.getAllPublicProjects(pageable);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ProjectDto>> getUserProjects(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(defaultValue = "true") boolean publicOnly) {
        Page<ProjectDto> projects = projectManagementUseCase.getUserProjects(userId, pageable, publicOnly);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/my")
    public ResponseEntity<Page<ProjectDto>> getMyProjects(
            @CurrentUser UserPrincipal userPrincipal,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProjectDto> projects = projectManagementUseCase.getUserProjects(userPrincipal.getId(), pageable, false);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/featured")
    public ResponseEntity<Page<ProjectDto>> getFeaturedProjects(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProjectDto> projects = projectManagementUseCase.getFeaturedProjects(pageable);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDto>> searchProjects(@RequestParam String title) {
        List<ProjectDto> projects = projectManagementUseCase.searchProjects(title);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/technology/{technology}")
    public ResponseEntity<List<ProjectDto>> getProjectsByTechnology(@PathVariable String technology) {
        List<ProjectDto> projects = projectManagementUseCase.getProjectsByTechnology(technology);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<ProjectDto>> getProjectsByTag(@PathVariable String tag) {
        List<ProjectDto> projects = projectManagementUseCase.getProjectsByTag(tag);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/trending")
    public ResponseEntity<List<ProjectDto>> getTrendingProjects() {
        List<ProjectDto> projects = projectManagementUseCase.getTrendingProjects();
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<ProjectDto>> getPopularProjects() {
        List<ProjectDto> projects = projectManagementUseCase.getPopularProjects();
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<ProjectDto>> getRecentProjects() {
        List<ProjectDto> projects = projectManagementUseCase.getRecentProjects();
        return ResponseEntity.ok(projects);
    }
    
    @PostMapping("/{projectId}/like")
    public ResponseEntity<ProjectDto> likeProject(@PathVariable Long projectId) {
        ProjectDto project = projectManagementUseCase.likeProject(projectId);
        return ResponseEntity.ok(project);
    }
    
    @DeleteMapping("/{projectId}/like")
    public ResponseEntity<ProjectDto> unlikeProject(@PathVariable Long projectId) {
        ProjectDto project = projectManagementUseCase.unlikeProject(projectId);
        return ResponseEntity.ok(project);
    }
    
    @PostMapping("/{projectId}/toggle-featured")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectDto> toggleFeatured(@PathVariable Long projectId) {
        ProjectDto project = projectManagementUseCase.toggleFeatured(projectId);
        return ResponseEntity.ok(project);
    }
    
    @GetMapping("/stats/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalProjectCount() {
        long count = projectManagementUseCase.getTotalProjectCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/user/{userId}/stats/count")
    public ResponseEntity<Long> getUserProjectCount(@PathVariable Long userId) {
        long count = projectManagementUseCase.getUserProjectCount(userId);
        return ResponseEntity.ok(count);
    }
}
