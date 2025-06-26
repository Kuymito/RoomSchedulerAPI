package org.example.roomschedulerapi.classroomscheduler.config; // Or your config package

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply to all API endpoints under /api/
                .allowedOrigins(
                        "http://localhost:3000",                        // For local development
                        "https://jaybird-new-previously.ngrok-free.app" // Your specific ngrok URL
                        // You could also use a wildcard if your ngrok subdomain changes: "https://*.ngrok-free.app"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // All methods your API uses
                .allowedHeaders("*")    // Allow all standard headers
                .allowCredentials(true) // Allow cookies and authentication headers
                .maxAge(3600);          // Cache preflight response for 1 hour
    }
}