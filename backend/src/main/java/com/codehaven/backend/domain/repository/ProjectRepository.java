package com.codehaven.backend.domain.repository;

import com.codehaven.backend.domain.model.Project;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    
    Project save(Project project);
    
    Optional<Project> findById(Long id);
    
    Page<Project> findAll(Pageable pageable);
    
    Page<Project> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Project> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Page<Project> findByUserAndIsPublicTrueOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<Project> findByTitleContainingIgnoreCaseAndIsPublicTrue(String title);
    
    List<Project> findByTagsContainingAndIsPublicTrue(String tag);
    
    List<Project> findByTechnologiesContainingAndIsPublicTrue(String technology);
    
    Page<Project> findByIsFeaturedTrueAndIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    
    List<Project> findTrendingProjects();
    
    List<Project> findTopProjectsByLikes();
    
    List<Project> findRecentPublicProjects();
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByIdAndUserId(Long projectId, Long userId);
    
    long count();
    
    long countByUser(User user);
}
