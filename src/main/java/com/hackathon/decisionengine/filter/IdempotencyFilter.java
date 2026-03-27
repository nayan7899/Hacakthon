package com.hackathon.decisionengine.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private static final String IDEMPOTENCY_HEADER = "X-Idempotency-Key";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. We only care about POST requests (creating/executing workflows)
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract the unique key from the request headers
        String idempotencyKey = request.getHeader(IDEMPOTENCY_HEADER);

        // 3. Reject the request if the header is missing
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            log.warn("Request rejected: Missing X-Idempotency-Key header");
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing X-Idempotency-Key header. This system requires idempotency tracking.");
            return;
        }

        String redisKey = "idempotency:" + idempotencyKey;

        // 4. Try to save the key to Redis. 
        // setIfAbsent returns TRUE if the key is brand new. It returns FALSE if it already exists.
        Boolean isNewRequest = redisTemplate.opsForValue().setIfAbsent(redisKey, "PROCESSED", Duration.ofHours(24));

        if (Boolean.FALSE.equals(isNewRequest)) {
            log.warn("Duplicate request intercepted for key: {}", idempotencyKey);
            response.setStatus(HttpStatus.CONFLICT.value());
            response.getWriter().write("Duplicate request detected. A request with this Idempotency Key has already been processed.");
            return;
        }

        // 5. If it's a new request, let it pass through to the Controller!
        filterChain.doFilter(request, response);
    }
}