package com.qurbaniconnect.config;

import com.qurbaniconnect.dto.response.ApiResponse;
import com.qurbaniconnect.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("[Config] Creating PasswordEncoder bean with BCrypt strength=12");
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("[Config] Setting up SecurityFilterChain");
        
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        log.info("[Config] Disabling CSRF protection");
        log.info("[Config] Configuring CORS with allowed origins: {}", allowedOrigins);
        log.info("[Config] Setting session management to STATELESS");
        
        log.info("[Config] Configuring authorization rules:");
        log.info("[Config]   - OPTIONS requests: permit all");
        log.info("[Config]   - Public auth endpoints: permit all");
        log.info("[Config]   - /api/v1/public/**: permit all");
        log.info("[Config]   - Swagger UI and API docs: permit all");
        log.info("[Config]   - Actuator health: permit all");
        log.info("[Config]   - /api/v1/admin/**: requires ROLE_ADMIN");
        log.info("[Config]   - /api/v1/qasai/**: requires ROLE_QASAI");
        log.info("[Config]   - /api/v1/customers/**: requires ROLE_CUSTOMER");
        log.info("[Config]   - /api/v1/bookings/**: requires ROLE_CUSTOMER");
        log.info("[Config]   - /api/v1/reviews/**: requires ROLE_CUSTOMER");
        log.info("[Config]   - All other requests: require authentication");
        
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(c -> c.configurationSource(corsSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/v1/auth/register/**", "/api/v1/auth/login", "/api/v1/auth/refresh-token",
                        "/api/v1/auth/forgot-password", "/api/v1/auth/reset-password").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/v1/qasai/**").hasAuthority("ROLE_QASAI")
                .requestMatchers("/api/v1/customers/**").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers("/api/v1/bookings/direct", "/api/v1/bookings/admin-assisted", "/api/v1/bookings/my-bookings")
                    .hasAuthority("ROLE_CUSTOMER")
                .requestMatchers("/api/v1/reviews/**").hasAuthority("ROLE_CUSTOMER")
                .anyRequest().authenticated())
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> {
                    log.warn("[Config] Authentication entry point triggered for {} {}", req.getMethod(), req.getRequestURI());
                    res.setStatus(HttpStatus.UNAUTHORIZED.value());
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    mapper.writeValue(res.getOutputStream(), ApiResponse.error("Authentication required or token invalid"));
                })
                .accessDeniedHandler((req, res, ex) -> {
                    log.warn("[Config] Access denied for {} {}", req.getMethod(), req.getRequestURI());
                    res.setStatus(HttpStatus.FORBIDDEN.value());
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    mapper.writeValue(res.getOutputStream(), ApiResponse.error("You do not have permission to perform this action"));
                }))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        log.info("[Config] Adding JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter");
        log.info("[Config] SecurityFilterChain setup completed");
        return http.build();
    }

    private CorsConfigurationSource corsSource() {
        log.debug("[Config] Configuring CORS source");
        CorsConfiguration cfg = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList();
        cfg.setAllowedOrigins(origins);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        cfg.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        log.info("[Config] CORS configured with origins: {}", origins);
        return src;
    }
}
