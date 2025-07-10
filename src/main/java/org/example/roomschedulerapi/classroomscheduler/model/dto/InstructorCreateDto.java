package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Data
public class InstructorCreateDto {
    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @Email
    @NotEmpty
    private String email;

    private String phone;
    private String degree;
    private String major;
    private String address;
    private String profile; // For the base64 image string
    private Long departmentId;

    // These are hardcoded in your frontend service but should be in the DTO
    private String password;
    private Long roleId;
}