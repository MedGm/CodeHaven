package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.Project;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {
    
    private final JpaProjectRepository jpaProjectRepository;
    
    @Override
    public Project save(Project project) {
        return jpaProjectRepository.save(project);
    }
    
    @Override
    public Optional<Project> findById(Long id) {
        return jpaProjectRepository.findById(id);
    }
    
    @Override
    public Page<Project> findAll(Pageable pageable) {
        return jpaProjectRepository.findAll(pageable);
    }
    
    @Override
    public Page<Project> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable) {
        return jpaProjectRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }
    
    @Override
    public Page<Project> findByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
        return jpaProjectRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    public Page<Project> findByUserAndIsPublicTrueOrderByCreatedAtDesc(User user, Pageable pageable) {
        return jpaProjectRepository.findByUserAndIsPublicTrueOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    public List<Project> findByTitleContainingIgnoreCaseAndIsPublicTrue(String title) {
        return jpaProjectRepository.findByTitleContainingIgnoreCaseAndIsPublicTrue(title);
    }
    
    @Override
    public List<Project> findByTagsContainingAndIsPublicTrue(String tag) {
        return jpaProjectRepository.findByTagsContainingAndIsPublicTrue(tag);
    }
    
    @Override
    public List<Project> findByTechnologiesContainingAndIsPublicTrue(String technology) {
        return jpaProjectRepository.findByTechnologiesContainingAndIsPublicTrue(technology);
    }
    
    @Override
    public Page<Project> findByIsFeaturedTrueAndIsPublicTrueOrderByCreatedAtDesc(Pageable pageable) {
        return jpaProjectRepository.findByIsFeaturedTrueAndIsPublicTrueOrderByCreatedAtDesc(pageable);
    }
    
    @Override
    public List<Project> findTrendingProjects() {
        // Get trending projects from the last 30 days
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        return jpaProjectRepository.findTrendingProjects(since, PageRequest.of(0, 10));
    }
    
    @Override
    public List<Project> findTopProjectsByLikes() {
        return jpaProjectRepository.findTop10ByIsPublicTrueOrderByLikesDesc();
    }
    
    @Override
    public List<Project> findRecentPublicProjects() {
        return jpaProjectRepository.findTop10ByIsPublicTrueOrderByCreatedAtDesc();
    }
    
    @Override
    public void deleteById(Long id) {
        jpaProjectRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaProjectRepository.existsById(id);
    }
    
    @Override
    public boolean existsByIdAndUserId(Long projectId, Long userId) {
        return jpaProjectRepository.existsByIdAndUserId(projectId, userId);
    }
    
    @Override
    public long count() {
        return jpaProjectRepository.count();
    }
    
    @Override
    public long countByUser(User user) {
        return jpaProjectRepository.countByUser(user);
    }
}
