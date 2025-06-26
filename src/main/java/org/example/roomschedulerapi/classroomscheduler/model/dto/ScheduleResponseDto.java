package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDto {
    private Long scheduleId;
    private Long classId;
    private String className;
    private String day;
    private Long roomId;
    private String roomName;
    private String buildingName; // Added building for more context

    public ScheduleResponseDto(Long scheduleId, Long classId, String className, Long roomId, String roomName, String buildingName, String day) {
        this.scheduleId = scheduleId;
        this.classId = classId;
        this.className = className;
        this.roomId = roomId;
        this.roomName = roomName;
        this.buildingName = buildingName;
        this.day = day;
    }
}