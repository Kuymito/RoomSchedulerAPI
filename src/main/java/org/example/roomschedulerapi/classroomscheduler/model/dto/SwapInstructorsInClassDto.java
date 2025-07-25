package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Data;

@Data
public class SwapInstructorsInClassDto {
    private Long classId;
    private String fromDayOfWeek;
    private String toDayOfWeek;
}