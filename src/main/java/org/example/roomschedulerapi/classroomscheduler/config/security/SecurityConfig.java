package org.example.roomschedulerapi.classroomscheduler.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource; // If you need CORS

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    // Remove UserDetailsService and other repository injections from here
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Apply your CORS settings
                .csrf(csrf -> csrf.disable()) // ✅ **DISABLE CSRF PROTECTION**
                .authorizeHttpRequests(auth -> auth
                        // ✅ **PERMIT ALL REQUESTS TO AUTH ENDPOINTS**
                        .requestMatchers(
                                "/api/v1/auth/**", // This covers /login and /register
                                "/swagger-ui/**",   // Allow access to Swagger UI
                                "/v3/api-docs/**"   // Allow access to OpenAPI docs
                        ).permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}