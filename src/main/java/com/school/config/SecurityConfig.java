package com.school.config;

import com.school.auth.filter.JwtAuthFilter;
import com.school.common.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // --- Whitelist Public Endpoints ---
                .requestMatchers("/auth/login", "/auth/refresh-token").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/webjars/**").permitAll()
                // Fix for requests still going to the old /api/v1 prefix
                .requestMatchers("/api/v1/v3/api-docs/**", "/api/v1/swagger-ui.html", "/api/v1/swagger-ui/**").permitAll()

                // --- Admin-Only Endpoints ---
                .requestMatchers("/users").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/students").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/teachers").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/academic-years/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/departments/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/subjects/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/classes/**").hasRole(Role.ADMIN.name())
                
                // --- Teacher & Admin Endpoints ---
                .requestMatchers("/files/**").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.POST, "/attendance/mark").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.PUT, "/attendance/**").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.POST, "/assignments").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.PUT, "/assignments/**").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.DELETE, "/assignments/**").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers("/submissions/*/grade").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())

                // --- Student-Only Endpoints ---
                .requestMatchers(HttpMethod.POST, "/submissions").hasRole(Role.STUDENT.name())

                // --- Catch-All: All other requests must be authenticated ---
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
