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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    // In SecurityConfig.java

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 1. Public Endpoints (Accessible to everyone, no token needed)

                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/otp/**"

                        ).permitAll()

                        // 2. Admin-Only Write Permissions
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/instructor", "/api/v1/class", "/api/v1/room"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/instructor/**", "/api/v1/class/**", "/api/v1/room/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                 "/api/v1/class/**", "/api/v1/room/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/instructor/**", "/api/v1/class/**", "/api/v1/room/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/change-requests/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/change-requests/*/deny").hasRole("ADMIN")

                        // Rule for creating change requests
                        .requestMatchers(HttpMethod.POST, "/api/v1/change-requests").hasAnyRole("ADMIN", "INSTRUCTOR")

                        // Rule for fetching all requests (for Admin dashboard)
                        .requestMatchers(HttpMethod.GET, "/api/v1/change-requests").hasAnyRole("ADMIN", "INSTRUCTOR")

                        // Rule for notifications
                        .requestMatchers("/api/v1/notifications/**").hasAnyRole("ADMIN", "INSTRUCTOR")

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/instructor/**").hasAnyRole("ADMIN", "INSTRUCTOR")

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/schedule/**").hasRole("ADMIN")



                        // 3. General Read Permissions for any authenticated user (Admin or Instructor)
                        .requestMatchers(HttpMethod.GET, "/api/v1/instructor/**", "/api/v1/room/**", "/api/v1/class/**", "/api/v1/department/**").authenticated()

                        // 4. Fallback Rule: Any other request must be authenticated
                        .anyRequest().authenticated()


                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                // Ensure your custom JWT filter runs before the standard Spring Security filters
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Arrays.asList("https://roomscheduler-1096936981338.europe-west1.run.app", "http://localhost:3000")); 

                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                // This applies the CORS policy to all API paths
                source.registerCorsConfiguration("/**", configuration); 
                return source;
        }
}