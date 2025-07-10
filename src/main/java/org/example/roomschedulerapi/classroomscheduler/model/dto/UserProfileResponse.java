package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileResponse {
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;
    private String profile; // The new field for the profile picture URL
    private String department;
    private String major;
    private String phone;
    private String degree;
    private Long id;
}