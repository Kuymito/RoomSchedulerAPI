package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDto {
    private Long scheduleId;
    private Long classId;
    private String className;
    private Long roomId;
    private String roomName;
    private String buildingName; // Added building for more context
}