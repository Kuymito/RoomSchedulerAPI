package org.example.roomschedulerapi.classroomscheduler.model.dto; // Adjust to your package

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructorResponseDto {
    private Long instructorId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String degree;
    private String major;
    private String profile;
    private String address;
    private boolean isArchived;
    private String roleName;
    private Long departmentId;
    private String departmentName;

    public InstructorResponseDto() {}

    public InstructorResponseDto(Long instructorId, String firstName, String lastName, String email, String phone, String degree, String major, String profile, String address, boolean isArchived, String roleName, Long departmentId, String departmentName) {
        this.instructorId = instructorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.degree = degree;
        this.major = major;
        this.profile = profile;
        this.address = address;
        this.isArchived = isArchived;
        this.roleName = roleName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }
}