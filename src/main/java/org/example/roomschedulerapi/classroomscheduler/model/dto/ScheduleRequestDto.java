package org.example.roomschedulerapi.classroomscheduler.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleRequestDto {
    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Room ID is required")
    private Long roomId;
}