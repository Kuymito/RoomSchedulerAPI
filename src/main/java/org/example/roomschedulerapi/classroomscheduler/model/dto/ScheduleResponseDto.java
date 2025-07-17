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
    private List<DayDetailDto> dayDetails;
    private String year;
    private String semester;
    private ShiftResponseDto shift;

    // Original Room Details
    private Long roomId;
    private String roomName;
    private String buildingName;

    // General Details
    private String majorName;
    private boolean isArchived;
    private String eventName;

    // Temporary Room Details
    private Long temporaryRoomId;
    private String temporaryRoomName;
    private String temporaryBuildingName;
}