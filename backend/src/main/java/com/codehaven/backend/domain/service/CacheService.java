package com.codehaven.backend.domain.service;

import java.util.List;
import java.util.Optional;

public interface CacheService {
    
    void put(String key, Object value);
    
    void put(String key, Object value, long timeoutInSeconds);
    
    <T> Optional<T> get(String key, Class<T> type);
    
    void delete(String key);
    
    void deletePattern(String pattern);
    
    boolean exists(String key);
    
    void increment(String key);
    
    void increment(String key, long delta);
    
    Long getCounter(String key);
    
    void setList(String key, List<?> list);
    
    <T> List<T> getList(String key, Class<T> type);
    
    void addToList(String key, Object value);
    
    void removeFromList(String key, Object value);
    
    void clearAll();
    
    // Cache keys constants
    String TRENDING_PROJECTS = "trending:projects";
    String TRENDING_BLOGS = "trending:blogs";
    String POPULAR_PROJECTS = "popular:projects";
    String POPULAR_BLOGS = "popular:blogs";
    String RECENT_PROJECTS = "recent:projects";
    String RECENT_BLOGS = "recent:blogs";
    String USER_PROFILE = "user:profile:";
    String PROJECT_VIEWS = "project:views:";
    String BLOG_VIEWS = "blog:views:";
}
