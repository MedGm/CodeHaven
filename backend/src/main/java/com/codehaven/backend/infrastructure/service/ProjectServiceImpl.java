package com.codehaven.backend.infrastructure.service;

import com.codehaven.backend.domain.model.Project;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.ProjectRepository;
import com.codehaven.backend.domain.repository.UserRepository;
import com.codehaven.backend.domain.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
    
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    
    @Override
    public Project createProject(Project project) {
        log.info("Creating new project '{}'", project.getTitle());
        return projectRepository.save(project);
    }
    
    @Override
    public Project updateProject(Long projectId, Project projectUpdate) {
        log.info("Updating project ID: {}", projectId);
        
        Project existingProject = findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));
        
        // Update only non-null fields
        if (projectUpdate.getTitle() != null) {
            existingProject.setTitle(projectUpdate.getTitle());
        }
        
        if (projectUpdate.getDescription() != null) {
            existingProject.setDescription(projectUpdate.getDescription());
        }
        
        if (projectUpdate.getRepoUrl() != null) {
            existingProject.setRepoUrl(projectUpdate.getRepoUrl());
        }
        
        if (projectUpdate.getDemoUrl() != null) {
            existingProject.setDemoUrl(projectUpdate.getDemoUrl());
        }
        
        if (projectUpdate.getTechnologies() != null) {
            existingProject.setTechnologies(projectUpdate.getTechnologies());
        }
        
        if (projectUpdate.getTags() != null) {
            existingProject.setTags(projectUpdate.getTags());
        }
        
        if (projectUpdate.getIsPublic() != null) {
            existingProject.setIsPublic(projectUpdate.getIsPublic());
        }
        
        return projectRepository.save(existingProject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Project> findById(Long projectId) {
        return projectRepository.findById(projectId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Project> findAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Project> findPublicProjects(Pageable pageable) {
        return projectRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Project> findProjectsByUser(User user, Pageable pageable) {
        return projectRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Project> findPublicProjectsByUser(User user, Pageable pageable) {
        return projectRepository.findByUserAndIsPublicTrueOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Project> searchProjectsByTitle(String title) {
        return projectRepository.findByTitleContainingIgnoreCaseAndIsPublicTrue(title);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Project> findProjectsByTag(String tag) {
        return projectRepository.findByTagsContainingAndIsPublicTrue(tag);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Project> findProjectsByTechnology(String technology) {
        return projectRepository.findByTechnologiesContainingAndIsPublicTrue(technology);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Project> findFeaturedProjects(Pageable pageable) {
        return projectRepository.findByIsFeaturedTrueAndIsPublicTrueOrderByCreatedAtDesc(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Project> getTrendingProjects() {
        return projectRepository.findTrendingProjects();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Project> getPopularProjects() {
        return projectRepository.findTopProjectsByLikes();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Project> getRecentProjects() {
        return projectRepository.findRecentPublicProjects();
    }
    
    @Override
    public Project incrementViews(Long projectId) {
        log.debug("Incrementing view count for project ID: {}", projectId);
        
        Project project = findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));
        
        project.setViews(project.getViews() + 1);
        return projectRepository.save(project);
    }
    
    @Override
    public Project likeProject(Long projectId) {
        log.debug("Liking project ID: {}", projectId);
        
        Project project = findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));
        
        project.setLikes(project.getLikes() + 1);
        return projectRepository.save(project);
    }
    
    @Override
    public Project unlikeProject(Long projectId) {
        log.debug("Unliking project ID: {}", projectId);
        
        Project project = findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));
        
        if (project.getLikes() > 0) {
            project.setLikes(project.getLikes() - 1);
        }
        return projectRepository.save(project);
    }
    
    @Override
    public void deleteProject(Long projectId) {
        log.info("Deleting project ID: {}", projectId);
        
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project not found with ID: " + projectId);
        }
        
        projectRepository.deleteById(projectId);
    }
    
    @Override
    public Project toggleFeatured(Long projectId) {
        log.info("Toggling featured status for project ID: {}", projectId);
        
        Project project = findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));
        
        project.setIsFeatured(!project.getIsFeatured());
        return projectRepository.save(project);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalProjectCount() {
        return projectRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUserProjectCount(User user) {
        return projectRepository.countByUser(user);
    }
    
    // Additional utility methods
    public Project createProjectForUser(Project project, Long userId) {
        log.info("Creating new project '{}' for user ID: {}", project.getTitle(), userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        project.setUser(user);
        return projectRepository.save(project);
    }
    
    public Project updateProjectForUser(Long projectId, Project projectUpdate, Long userId) {
        log.info("Updating project ID: {} by user ID: {}", projectId, userId);
        
        Project existingProject = findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));
        
        // Check if user owns the project
        if (!existingProject.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to update this project");
        }
        
        return updateProject(projectId, projectUpdate);
    }
    
    public void deleteProjectForUser(Long projectId, Long userId) {
        log.info("Deleting project ID: {} by user ID: {}", projectId, userId);
        
        Project project = findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));
        
        // Check if user owns the project
        if (!project.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to delete this project");
        }
        
        deleteProject(projectId);
    }
    
    public boolean isProjectOwnedByUser(Long projectId, Long userId) {
        return projectRepository.existsByIdAndUserId(projectId, userId);
    }
}
