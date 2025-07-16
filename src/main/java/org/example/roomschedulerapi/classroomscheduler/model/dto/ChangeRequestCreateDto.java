package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ChangeRequestCreateDto {
    // The instructorId field is removed from here
    private Long scheduleId;
    private Long newRoomId;
    private LocalDate effectiveDate;
    private String description;
    private String eventName;
}