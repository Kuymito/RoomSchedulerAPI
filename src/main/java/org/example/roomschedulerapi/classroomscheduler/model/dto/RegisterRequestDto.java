package org.example.roomschedulerapi.classroomscheduler.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private String phone;
    private String degree;
    private String major;
    private String address;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Role ID is required")
    private Long roleId; // e.g., ID for "ROLE_INSTRUCTOR"
}