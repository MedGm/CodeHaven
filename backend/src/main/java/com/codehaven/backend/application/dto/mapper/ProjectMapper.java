package com.codehaven.backend.application.dto.mapper;

import com.codehaven.backend.application.dto.project.CreateProjectRequest;
import com.codehaven.backend.application.dto.project.ProjectDto;
import com.codehaven.backend.application.dto.project.UpdateProjectRequest;
import com.codehaven.backend.domain.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    
    public ProjectDto toProjectDto(Project project) {
        if (project == null) {
            return null;
        }
        
        return ProjectDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .repoUrl(project.getRepoUrl())
                .demoUrl(project.getDemoUrl())
                .technologies(project.getTechnologies())
                .tags(project.getTags())
                .isPublic(project.getIsPublic())
                .isFeatured(project.getIsFeatured())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .userId(project.getUser() != null ? project.getUser().getId() : null)
                .username(project.getUser() != null ? project.getUser().getUsername() : null)
                .userAvatarUrl(project.getUser() != null ? project.getUser().getAvatarUrl() : null)
                .views(project.getViews())
                .likes(project.getLikes())
                .collaborators(project.getCollaborators())
                .build();
    }
    
    public Project fromCreateRequest(CreateProjectRequest request) {
        if (request == null) {
            return null;
        }
        
        return Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .repoUrl(request.getRepoUrl())
                .demoUrl(request.getDemoUrl())
                .technologies(request.getTechnologies())
                .tags(request.getTags())
                .isPublic(request.getIsPublic())
                .build();
    }
    
    public Project fromUpdateRequest(UpdateProjectRequest request) {
        if (request == null) {
            return null;
        }
        
        return Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .repoUrl(request.getRepoUrl())
                .demoUrl(request.getDemoUrl())
                .technologies(request.getTechnologies())
                .tags(request.getTags())
                .isPublic(request.getIsPublic())
                .build();
    }
}
