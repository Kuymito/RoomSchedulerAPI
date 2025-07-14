package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Data;

@Data
public class AdminUpdateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profile;
}