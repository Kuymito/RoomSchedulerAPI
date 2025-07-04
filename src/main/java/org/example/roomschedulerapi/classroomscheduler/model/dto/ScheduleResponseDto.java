package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.Shift;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDto {
    private Long scheduleId;
    private Long classId;
    private String className;
    private String day;
    private String year;       // Mapped from Class's generation
    private String semester;
    private ShiftResponseDto shift;
    private Long roomId;
    private String roomName;
    private String buildingName; // Added building for more context




}