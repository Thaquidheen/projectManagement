package com.company.erp.common.config;

import com.company.erp.common.security.CustomUserDetailsService;
import com.company.erp.common.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
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
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF as we use JWT tokens
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Set session management to stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Set permissions on endpoints
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - FIXED PATH PATTERNS
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()  // Added this for your current request pattern
                        .requestMatchers("/api/actuator/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/swagger-resources/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/api/webjars/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/api/").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Health check endpoints
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/api/health").permitAll()

                        // Admin only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/roles").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("SUPER_ADMIN")

                        // Project management endpoints
                        .requestMatchers(HttpMethod.POST, "/api/projects").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/projects/*/assign").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/projects").hasAnyRole("SUPER_ADMIN", "PROJECT_MANAGER", "ACCOUNT_MANAGER")

                        // Quotation endpoints
                        .requestMatchers(HttpMethod.POST, "/api/quotations").hasAnyRole("PROJECT_MANAGER", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/quotations/*").hasAnyRole("PROJECT_MANAGER", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/quotations").hasAnyRole("PROJECT_MANAGER", "ACCOUNT_MANAGER", "SUPER_ADMIN")

                        // Approval endpoints
                        .requestMatchers("/api/approvals/**").hasAnyRole("ACCOUNT_MANAGER", "SUPER_ADMIN")

                        // Payment endpoints
                        .requestMatchers("/api/payments/**").hasAnyRole("ACCOUNT_MANAGER", "SUPER_ADMIN")

                        // Reports endpoints
                        .requestMatchers("/api/reports/**").hasAnyRole("ACCOUNT_MANAGER", "SUPER_ADMIN")

                        // Profile endpoints (any authenticated user)
                        .requestMatchers(HttpMethod.GET, "/api/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/profile").authenticated()

                        // All other requests need authentication
                        .anyRequest().authenticated()
                )

                // Set authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specific origins (configure based on environment)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173",
                "https://erp.yourcompany.com"
        ));

        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}