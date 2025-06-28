package com.codehaven.backend.application.usecase;

import com.codehaven.backend.application.dto.mapper.ProjectMapper;
import com.codehaven.backend.application.dto.project.CreateProjectRequest;
import com.codehaven.backend.application.dto.project.ProjectDto;
import com.codehaven.backend.application.dto.project.UpdateProjectRequest;
import com.codehaven.backend.domain.model.Project;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.service.ProjectService;
import com.codehaven.backend.domain.service.UserService;
import com.codehaven.backend.infrastructure.service.ProjectServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectManagementUseCase {
    
    private final ProjectService projectService;
    private final ProjectServiceImpl projectServiceImpl; // For user-specific methods
    private final UserService userService;
    private final ProjectMapper projectMapper;
    
    public ProjectDto createProject(CreateProjectRequest request, Long userId) {
        Project project = projectMapper.fromCreateRequest(request);
        Project createdProject = projectServiceImpl.createProjectForUser(project, userId);
        return projectMapper.toProjectDto(createdProject);
    }
    
    public ProjectDto getProject(Long projectId) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));
        return projectMapper.toProjectDto(project);
    }
    
    public ProjectDto getProjectAndIncrementViews(Long projectId) {
        Project project = projectService.incrementViews(projectId);
        return projectMapper.toProjectDto(project);
    }
    
    public ProjectDto updateProject(Long projectId, UpdateProjectRequest request, Long userId) {
        Project projectUpdate = projectMapper.fromUpdateRequest(request);
        Project updatedProject = projectServiceImpl.updateProjectForUser(projectId, projectUpdate, userId);
        return projectMapper.toProjectDto(updatedProject);
    }
    
    public void deleteProject(Long projectId, Long userId) {
        projectServiceImpl.deleteProjectForUser(projectId, userId);
    }
    
    public Page<ProjectDto> getAllPublicProjects(Pageable pageable) {
        return projectService.findPublicProjects(pageable)
                .map(projectMapper::toProjectDto);
    }
    
    public Page<ProjectDto> getUserProjects(Long userId, Pageable pageable, boolean publicOnly) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        Page<Project> projects = publicOnly 
            ? projectService.findPublicProjectsByUser(user, pageable)
            : projectService.findProjectsByUser(user, pageable);
            
        return projects.map(projectMapper::toProjectDto);
    }
    
    public Page<ProjectDto> getFeaturedProjects(Pageable pageable) {
        return projectService.findFeaturedProjects(pageable)
                .map(projectMapper::toProjectDto);
    }
    
    public List<ProjectDto> searchProjects(String title) {
        return projectService.searchProjectsByTitle(title)
                .stream()
                .map(projectMapper::toProjectDto)
                .collect(Collectors.toList());
    }
    
    public List<ProjectDto> getProjectsByTechnology(String technology) {
        return projectService.findProjectsByTechnology(technology)
                .stream()
                .map(projectMapper::toProjectDto)
                .collect(Collectors.toList());
    }
    
    public List<ProjectDto> getProjectsByTag(String tag) {
        return projectService.findProjectsByTag(tag)
                .stream()
                .map(projectMapper::toProjectDto)
                .collect(Collectors.toList());
    }
    
    public List<ProjectDto> getTrendingProjects() {
        return projectService.getTrendingProjects()
                .stream()
                .map(projectMapper::toProjectDto)
                .collect(Collectors.toList());
    }
    
    public List<ProjectDto> getPopularProjects() {
        return projectService.getPopularProjects()
                .stream()
                .map(projectMapper::toProjectDto)
                .collect(Collectors.toList());
    }
    
    public List<ProjectDto> getRecentProjects() {
        return projectService.getRecentProjects()
                .stream()
                .map(projectMapper::toProjectDto)
                .collect(Collectors.toList());
    }
    
    public ProjectDto likeProject(Long projectId) {
        Project project = projectService.likeProject(projectId);
        return projectMapper.toProjectDto(project);
    }
    
    public ProjectDto unlikeProject(Long projectId) {
        Project project = projectService.unlikeProject(projectId);
        return projectMapper.toProjectDto(project);
    }
    
    public ProjectDto toggleFeatured(Long projectId) {
        Project project = projectService.toggleFeatured(projectId);
        return projectMapper.toProjectDto(project);
    }
    
    public long getTotalProjectCount() {
        return projectService.getTotalProjectCount();
    }
    
    public long getUserProjectCount(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return projectService.getUserProjectCount(user);
    }
    
    public boolean isProjectOwnedByUser(Long projectId, Long userId) {
        return projectServiceImpl.isProjectOwnedByUser(projectId, userId);
    }
}
