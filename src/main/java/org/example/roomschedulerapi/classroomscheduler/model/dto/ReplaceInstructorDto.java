package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Data;

@Data
public class ReplaceInstructorDto {
    private Long classId;
    private String dayOfWeek;
    private Long newInstructorId; // The ID of the instructor to be assigned
}