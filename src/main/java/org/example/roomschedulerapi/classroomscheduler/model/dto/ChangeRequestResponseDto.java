package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Data;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class ChangeRequestResponseDto {

    private Long requestId;
    private Long classId;
    private String className;
    private Long newRoomId;
    private String newRoomName;
    private Long newShiftId;
    private String newShiftName;
    private String description;
    private RequestStatus status;
    private OffsetDateTime requestedAt;
    private LocalDateTime dayOfChange;
}