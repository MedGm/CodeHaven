package com.codehaven.backend.domain.service;

import com.codehaven.backend.domain.model.Project;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    
    Project createProject(Project project);
    
    Project updateProject(Long projectId, Project projectUpdate);
    
    Optional<Project> findById(Long projectId);
    
    Page<Project> findAllProjects(Pageable pageable);
    
    Page<Project> findPublicProjects(Pageable pageable);
    
    Page<Project> findProjectsByUser(User user, Pageable pageable);
    
    Page<Project> findPublicProjectsByUser(User user, Pageable pageable);
    
    List<Project> searchProjectsByTitle(String title);
    
    List<Project> findProjectsByTag(String tag);
    
    List<Project> findProjectsByTechnology(String technology);
    
    Page<Project> findFeaturedProjects(Pageable pageable);
    
    List<Project> getTrendingProjects();
    
    List<Project> getPopularProjects();
    
    List<Project> getRecentProjects();
    
    Project incrementViews(Long projectId);
    
    Project likeProject(Long projectId);
    
    Project unlikeProject(Long projectId);
    
    void deleteProject(Long projectId);
    
    Project toggleFeatured(Long projectId);
    
    long getTotalProjectCount();
    
    long getUserProjectCount(User user);
}
