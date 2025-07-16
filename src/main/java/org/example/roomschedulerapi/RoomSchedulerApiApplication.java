package org.example.roomschedulerapi; // Or your actual main package

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(
        info = @Info(title = "Room Scheduler API", version = "v1", description = "API documentation for the Room Scheduler application"),
        security = {
                @SecurityRequirement(name = "bearerAuth") // Applies this security scheme globally to all endpoints
        }
)
@SecurityScheme(
        name = "bearerAuth", // A name for the security scheme (can be anything)
        description = "Enter JWT Bearer token to authorize",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP, // The type of security
        bearerFormat = "JWT",           // The format of the token
        in = SecuritySchemeIn.HEADER    // Where the token is located (in the request header)
)
@EnableAsync
public class RoomSchedulerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoomSchedulerApiApplication.class, args);
    }

}
