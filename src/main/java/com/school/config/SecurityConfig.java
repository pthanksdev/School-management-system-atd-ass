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

    // Swagger UI / OpenAPI endpoints
    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger UI — public
                .requestMatchers(SWAGGER_WHITELIST).permitAll()

                // Auth endpoints — public
                .requestMatchers("/auth/login", "/auth/refresh-token").permitAll()
                .requestMatchers("/actuator/health").permitAll()

                // Admin only
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/students").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/teachers").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/academic-years/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/departments/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/subjects/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/classes/**").hasRole(Role.ADMIN.name())
                .requestMatchers("/users").hasRole(Role.ADMIN.name())

                // Teacher + Admin
                .requestMatchers("/files/**").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.POST, "/attendance/mark").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.PUT, "/attendance/**").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.POST, "/assignments").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.PUT, "/assignments/**").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.DELETE, "/assignments/**").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                .requestMatchers("/submissions/*/grade").hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())

                // Student
                .requestMatchers(HttpMethod.POST, "/submissions").hasRole(Role.STUDENT.name())

                // All authenticated users
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}