package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class BlogRepositoryImpl implements BlogRepository {

    private final JpaBlogRepository jpaBlogRepository;

    @Autowired
    public BlogRepositoryImpl(JpaBlogRepository jpaBlogRepository) {
        this.jpaBlogRepository = jpaBlogRepository;
    }

    @Override
    public Blog save(Blog blog) {
        return jpaBlogRepository.save(blog);
    }

    @Override
    public Optional<Blog> findById(Long id) {
        return jpaBlogRepository.findById(id);
    }

    @Override
    public Page<Blog> findAll(Pageable pageable) {
        return jpaBlogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> findAllByStatus(Blog.Status status, Pageable pageable) {
        return jpaBlogRepository.findAllByStatus(status, pageable);
    }

    @Override
    public Page<Blog> findAllByUser(User user, Pageable pageable) {
        return jpaBlogRepository.findAllByUser(user, pageable);
    }

    @Override
    public Page<Blog> findAllByUserAndStatus(User user, Blog.Status status, Pageable pageable) {
        return jpaBlogRepository.findAllByUserAndStatus(user, status, pageable);
    }

    @Override
    public List<Blog> findByTitleContainingIgnoreCase(String title) {
        return jpaBlogRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Blog> findByTagsContainingIgnoreCase(String tag) {
        return jpaBlogRepository.findByTagsContaining(tag);
    }

    @Override
    public Page<Blog> findAllByIsFeaturedTrue(Pageable pageable) {
        return jpaBlogRepository.findAllByIsFeaturedTrue(pageable);
    }

    @Override
    public List<Blog> findTopByOrderByLikesDesc(int limit) {
        return jpaBlogRepository.findTop10ByOrderByLikesDesc();
    }

    @Override
    public List<Blog> findTopByOrderByViewsDesc(int limit) {
        return jpaBlogRepository.findTop10ByOrderByViewsDesc();
    }

    @Override
    public List<Blog> findRecentPublishedBlogs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return jpaBlogRepository.findRecentPublishedBlogs(pageable);
    }

    @Override
    public void deleteById(Long id) {
        jpaBlogRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaBlogRepository.count();
    }

    @Override
    public long countByUser(User user) {
        return jpaBlogRepository.countByUser(user);
    }

    @Override
    public long countByStatus(Blog.Status status) {
        return jpaBlogRepository.countByStatus(status);
    }

    @Override
    @Transactional
    public Blog incrementViews(Long blogId) {
        jpaBlogRepository.incrementViews(blogId);
        return jpaBlogRepository.findById(blogId).orElse(null);
    }

    @Override
    @Transactional
    public Blog incrementLikes(Long blogId) {
        jpaBlogRepository.incrementLikes(blogId);
        return jpaBlogRepository.findById(blogId).orElse(null);
    }

    @Override
    @Transactional
    public Blog decrementLikes(Long blogId) {
        jpaBlogRepository.decrementLikes(blogId);
        return jpaBlogRepository.findById(blogId).orElse(null);
    }
}
