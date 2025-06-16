package org.example.roomschedulerapi.classroomscheduler.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    // In your SecurityConfig.java file

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. Public Endpoints (Accessible to everyone)
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 2. Instructor Management Rules (Most Specific)
                        // Only ADMIN can create, update, or delete instructors
                        .requestMatchers(HttpMethod.POST, "/api/v1/instructor").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/instructor/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/instructor/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/instructor/**").hasRole("ADMIN")

                        // Any authenticated user (Admin or Instructor) can view instructor data
                        .requestMatchers(HttpMethod.GET, "/api/v1/instructor/**").authenticated()

                        // 3. Other Authenticated Endpoints
                        // Any authenticated user can view rooms and classes
                        .requestMatchers(HttpMethod.GET, "/api/v1/room/**", "/api/v1/class/**").authenticated()

                        // 4. Fallback Rule (Catch-all)
                        // Any other request that hasn't been matched yet must be authenticated
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}