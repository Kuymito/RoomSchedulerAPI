package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDto {

    private Long scheduleId;
    private Long classId;
    private String className;
    private List<DayDetailDto> dayDetails; // Contains all day-specific info
    private String year;
    private String semester;
    private ShiftResponseDto shift;
    private Long roomId;
    private String roomName;
    private String buildingName;
    private String majorName;
    private boolean isArchived;
}