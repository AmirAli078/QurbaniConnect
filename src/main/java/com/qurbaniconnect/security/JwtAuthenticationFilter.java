package com.qurbaniconnect.security;

import com.qurbaniconnect.entity.User;
import com.qurbaniconnect.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** Authenticates every request from the Bearer token; the user is re-read so status/role changes apply immediately. */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        log.debug("[Security] Processing request: {} {}", request.getMethod(), uri);
        
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            log.debug("[Security] No Authorization header found for {} {}", request.getMethod(), uri);
            chain.doFilter(request, response);
            return;
        }
        
        if (!header.startsWith("Bearer ")) {
            log.debug("[Security] Authorization header does not start with 'Bearer' for {} {}", request.getMethod(), uri);
            chain.doFilter(request, response);
            return;
        }
        
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.debug("[Security] Authentication already set in SecurityContext for {} {}", request.getMethod(), uri);
            chain.doFilter(request, response);
            return;
        }
        
        String token = header.substring(7).trim();
        log.debug("[Security] Extracted Bearer token, attempting validation");
        
        jwtService.validateAndGetUserId(token)
                .ifPresentOrElse(
                        userId -> {
                            log.debug("[Security] Token validated successfully, userId={}", userId);
                            userRepository.findWithRolesById(userId)
                                    .ifPresentOrElse(
                                            user -> {
                                                UserPrincipal principal = new UserPrincipal(user);
                                                if (principal.isEnabled()) {
                                                    var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                                                    SecurityContextHolder.getContext().setAuthentication(auth);
                                                    log.info("[Security] Authentication set for userId={}, roles={}, request={} {}", 
                                                            userId, principal.getAuthorities(), request.getMethod(), uri);
                                                } else {
                                                    log.warn("[Security] User principal is disabled for userId={}, request={} {}", userId, request.getMethod(), uri);
                                                }
                                            },
                                            () -> log.warn("[Security] User not found in database for userId={}, request={} {}", userId, request.getMethod(), uri)
                                    );
                        },
                        () -> log.warn("[Security] Token validation failed for {} {}", request.getMethod(), uri)
                );
        
        log.debug("[Security] Continuing filter chain for {} {}", request.getMethod(), uri);
        chain.doFilter(request, response);
    }
}
