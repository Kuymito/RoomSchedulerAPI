// File: src/main/java/com/yourpackage/WebConfig.java (adjust package name)
package org.example.roomschedulerapi.classroomscheduler.config; // Ensure this is your actual package

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("Attempting to configure CORS in WebConfig..."); // Add this log
        registry.addMapping("/api/**") // This pattern should cover /api/v1/room and /api/v1/instructor
                .allowedOrigins("http://localhost:3000") // Your frontend origin
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // All methods you use
                .allowedHeaders("*") // Allows all headers
                .allowCredentials(true) // If you handle credentials/cookies
                .maxAge(3600); // Cache preflight response for 1 hour
        System.out.println("CORS configuration for /api/** applied in WebConfig."); // Add this log
    }
}