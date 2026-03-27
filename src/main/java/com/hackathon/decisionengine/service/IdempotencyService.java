package com.hackathon.decisionengine.service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.hackathon.decisionengine.dto.WorkflowResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    /**
     * Check if a request with the given idempotency key has already been processed
     * @param idempotencyKey The idempotency key to check
     * @return true if already processed, false otherwise
     */
    public boolean isRequestProcessed(String idempotencyKey) {
        try {
            String key = buildKey(idempotencyKey);
            Boolean exists = redisTemplate.hasKey(key);
            log.debug("Checking idempotency key {}: {}", key, exists ? "EXISTS" : "NOT FOUND");
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Error checking idempotency for key: {}", idempotencyKey, e);
            return false;
        }
    }

    /**
     * Store the response for an idempotency key
     * @param idempotencyKey The idempotency key
     * @param response The response to store
     */
    public void storeResponse(String idempotencyKey, WorkflowResponse response) {
        try {
            String key = buildKey(idempotencyKey);
            redisTemplate.opsForValue().set(key, response, CACHE_TTL);
            log.debug("Stored response for idempotency key: {}", key);
        } catch (Exception e) {
            log.error("Error storing response for idempotency key: {}", idempotencyKey, e);
        }
    }

    /**
     * Get the stored response for an idempotency key
     * @param idempotencyKey The idempotency key
     * @return The stored response or null if not found
     */
    public WorkflowResponse getStoredResponse(String idempotencyKey) {
        try {
            String key = buildKey(idempotencyKey);
            Object stored = redisTemplate.opsForValue().get(key);
            if (stored instanceof WorkflowResponse) {
                log.debug("Retrieved stored response for idempotency key: {}", key);
                return (WorkflowResponse) stored;
            }
            return null;
        } catch (Exception e) {
            log.error("Error retrieving stored response for idempotency key: {}", idempotencyKey, e);
            return null;
        }
    }

    /**
     * Mark a request as processed (alternative to storing full response)
     * @param idempotencyKey The idempotency key
     */
    public void markAsProcessed(String idempotencyKey) {
        try {
            String key = buildKey(idempotencyKey);
            redisTemplate.opsForValue().set(key, "PROCESSED", CACHE_TTL);
            log.debug("Marked request as processed for idempotency key: {}", key);
        } catch (Exception e) {
            log.error("Error marking request as processed for idempotency key: {}", idempotencyKey, e);
        }
    }

    /**
     * Clear an idempotency key (for testing or manual cleanup)
     * @param idempotencyKey The idempotency key to clear
     */
    public void clearIdempotencyKey(String idempotencyKey) {
        try {
            String key = buildKey(idempotencyKey);
            Boolean deleted = redisTemplate.delete(key);
            log.debug("Cleared idempotency key {}: {}", key, deleted ? "SUCCESS" : "NOT FOUND");
        } catch (Exception e) {
            log.error("Error clearing idempotency key: {}", idempotencyKey, e);
        }
    }

    /**
     * Build the Redis key for idempotency
     */
    private String buildKey(String idempotencyKey) {
        return IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
    }

    /**
     * Get TTL for a key (useful for monitoring)
     */
    public Long getTtl(String idempotencyKey) {
        try {
            String key = buildKey(idempotencyKey);
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error getting TTL for idempotency key: {}", idempotencyKey, e);
            return null;
        }
    }
}
