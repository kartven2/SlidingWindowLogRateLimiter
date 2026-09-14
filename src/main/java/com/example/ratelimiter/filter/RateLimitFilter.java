package com.example.ratelimiter.filter;

import com.example.ratelimiter.core.RateLimiter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RateLimitFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    private final RateLimiter limiter;

    // Spring automatically injects the Limiter we created in RedisConfig
    public RateLimitFilter(RateLimiter limiter) {
        this.limiter = limiter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Prevent browser caching so we can properly test rate limiting
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        String clientIp = httpRequest.getRemoteAddr();
        String path = httpRequest.getRequestURI();
        
        logger.info("[RateLimitFilter] Request from IP: {} for Path: {}", clientIp, path);

        boolean isAllowed = limiter.isAllowed(clientIp);

        if (isAllowed) {
            logger.info("[RateLimitFilter] ALLOWED {}", clientIp);
            chain.doFilter(request, response);
        } else {
            logger.warn("[RateLimitFilter] BLOCKED {}", clientIp);
            httpResponse.setStatus(429); // 429 Too Many Requests
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Too Many Requests - Rate limit exceeded\"}");
        }
    }
}
