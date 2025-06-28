package com.codehaven.backend.infrastructure.service;

import com.codehaven.backend.domain.service.CacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService implements CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    @Override
    public void put(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("Cached value for key: {}", key);
        } catch (Exception e) {
            log.error("Error caching value for key: {}", key, e);
        }
    }
    
    @Override
    public void put(String key, Object value, long timeoutInSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeoutInSeconds));
            log.debug("Cached value for key: {} with timeout: {} seconds", key, timeoutInSeconds);
        } catch (Exception e) {
            log.error("Error caching value for key: {} with timeout", key, e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            
            if (type.isInstance(value)) {
                return Optional.of((T) value);
            }
            
            // Try to convert using ObjectMapper
            T convertedValue = objectMapper.convertValue(value, type);
            return Optional.of(convertedValue);
        } catch (Exception e) {
            log.error("Error retrieving cached value for key: {}", key, e);
            return Optional.empty();
        }
    }
    
    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Deleted cached value for key: {}", key);
        } catch (Exception e) {
            log.error("Error deleting cached value for key: {}", key, e);
        }
    }
    
    @Override
    public void deletePattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Deleted {} keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.error("Error deleting cached values for pattern: {}", pattern, e);
        }
    }
    
    @Override
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking existence of key: {}", key, e);
            return false;
        }
    }
    
    @Override
    public void increment(String key) {
        increment(key, 1L);
    }
    
    @Override
    public void increment(String key, long delta) {
        try {
            redisTemplate.opsForValue().increment(key, delta);
            log.debug("Incremented key: {} by: {}", key, delta);
        } catch (Exception e) {
            log.error("Error incrementing key: {}", key, e);
        }
    }
    
    @Override
    public Long getCounter(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return 0L;
        } catch (Exception e) {
            log.error("Error getting counter for key: {}", key, e);
            return 0L;
        }
    }
    
    @Override
    public void setList(String key, List<?> list) {
        try {
            redisTemplate.opsForList().rightPushAll(key, list.toArray());
            log.debug("Cached list for key: {} with {} items", key, list.size());
        } catch (Exception e) {
            log.error("Error caching list for key: {}", key, e);
        }
    }
    
    @Override
    public <T> List<T> getList(String key, Class<T> type) {
        try {
            List<Object> values = redisTemplate.opsForList().range(key, 0, -1);
            if (values == null || values.isEmpty()) {
                return List.of();
            }
            
            return values.stream()
                    .map(value -> objectMapper.convertValue(value, type))
                    .toList();
        } catch (Exception e) {
            log.error("Error retrieving cached list for key: {}", key, e);
            return List.of();
        }
    }
    
    @Override
    public void addToList(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            log.debug("Added value to list for key: {}", key);
        } catch (Exception e) {
            log.error("Error adding value to list for key: {}", key, e);
        }
    }
    
    @Override
    public void removeFromList(String key, Object value) {
        try {
            redisTemplate.opsForList().remove(key, 1, value);
            log.debug("Removed value from list for key: {}", key);
        } catch (Exception e) {
            log.error("Error removing value from list for key: {}", key, e);
        }
    }
    
    @Override
    public void clearAll() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Cleared all cached values ({} keys)", keys.size());
            }
        } catch (Exception e) {
            log.error("Error clearing all cached values", e);
        }
    }
}
