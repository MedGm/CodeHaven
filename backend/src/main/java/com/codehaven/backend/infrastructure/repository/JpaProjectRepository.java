package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.Project;
import com.codehaven.backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaProjectRepository extends JpaRepository<Project, Long> {
    
    Page<Project> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Project> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Page<Project> findByUserAndIsPublicTrueOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<Project> findByTitleContainingIgnoreCaseAndIsPublicTrue(String title);
    
    @Query("SELECT p FROM Project p WHERE :tag MEMBER OF p.tags AND p.isPublic = true")
    List<Project> findByTagsContainingAndIsPublicTrue(@Param("tag") String tag);
    
    @Query("SELECT p FROM Project p WHERE :technology MEMBER OF p.technologies AND p.isPublic = true")
    List<Project> findByTechnologiesContainingAndIsPublicTrue(@Param("technology") String technology);
    
    Page<Project> findByIsFeaturedTrueAndIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    
    List<Project> findTop10ByIsPublicTrueOrderByLikesDesc();
    
    List<Project> findTop10ByIsPublicTrueOrderByCreatedAtDesc();
    
    @Query("SELECT p FROM Project p WHERE p.isPublic = true AND p.createdAt >= :since ORDER BY (p.likes + p.views) DESC")
    List<Project> findTrendingProjects(@Param("since") LocalDateTime since, Pageable pageable);
    
    boolean existsByIdAndUserId(Long projectId, Long userId);
    
    long countByUser(User user);
}
