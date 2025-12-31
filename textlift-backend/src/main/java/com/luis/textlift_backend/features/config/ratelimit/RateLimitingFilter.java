package com.luis.textlift_backend.features.config.ratelimit;

import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final LoadingCache<String, Bucket> buckets;

    public RateLimitingFilter(LoadingCache<String, Bucket> buckets) {
        this.buckets = buckets;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException,IOException {
        //Rather than just storing IP addresses indefinitely, we can use Caffeine to
        //cache them, allowing for more dynamic rate limiting.

        String key = clientKey(request);
        Bucket bucket = buckets.get(key);
        if(bucket.tryConsume(1)){
            filterChain.doFilter(request, response);
        }
        else{
            response.setStatus(429);
            response.getWriter().write("{\"message\": \"Too many requests. Please try again later.\"}");
        }
    }

    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For"); //X-forward-for first IP or remote Addr
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }


}
