package org.example.roomschedulerapi.classroomscheduler.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component // ✅ This annotation makes it a Spring bean, so it can be autowired
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        System.err.println("Authentication error detected by JwtAuthenticationEntryPoint: " + authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "Authentication Failed: " + authException.getMessage());
        body.put("path", request.getServletPath());
        body.put("timestamp", LocalDateTime.now().toString());

        // Use ObjectMapper to serialize the map to JSON, which is more robust
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // To handle LocalDateTime correctly
        mapper.writeValue(response.getOutputStream(), body);
    }
}