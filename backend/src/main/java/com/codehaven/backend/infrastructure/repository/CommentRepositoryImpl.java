package com.codehaven.backend.infrastructure.repository;

import com.codehaven.backend.domain.model.Comment;
import com.codehaven.backend.domain.model.Blog;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CommentRepositoryImpl implements CommentRepository {

    private final JpaCommentRepository jpaCommentRepository;

    @Autowired
    public CommentRepositoryImpl(JpaCommentRepository jpaCommentRepository) {
        this.jpaCommentRepository = jpaCommentRepository;
    }

    @Override
    public Comment save(Comment comment) {
        return jpaCommentRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return jpaCommentRepository.findById(id);
    }

    @Override
    public Page<Comment> findAllByBlog(Blog blog, Pageable pageable) {
        return jpaCommentRepository.findAllByBlog(blog, pageable);
    }

    @Override
    public Page<Comment> findAllByUser(User user, Pageable pageable) {
        return jpaCommentRepository.findAllByUser(user, pageable);
    }

    @Override
    public List<Comment> findAllByParentComment(Comment parentComment) {
        return jpaCommentRepository.findAllByParentComment(parentComment);
    }

    @Override
    public List<Comment> findRootCommentsByBlog(Blog blog) {
        return jpaCommentRepository.findRootCommentsByBlog(blog);
    }

    @Override
    public void deleteById(Long id) {
        jpaCommentRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaCommentRepository.count();
    }

    @Override
    public long countByBlog(Blog blog) {
        return jpaCommentRepository.countByBlog(blog);
    }

    @Override
    public long countByUser(User user) {
        return jpaCommentRepository.countByUser(user);
    }

    @Override
    @Transactional
    public Comment incrementLikes(Long commentId) {
        jpaCommentRepository.incrementLikes(commentId);
        return jpaCommentRepository.findById(commentId).orElse(null);
    }

    @Override
    @Transactional
    public Comment decrementLikes(Long commentId) {
        jpaCommentRepository.decrementLikes(commentId);
        return jpaCommentRepository.findById(commentId).orElse(null);
    }
}
