package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Data;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class ChangeRequestResponseDto {
    private Long id;
    private Long scheduleId;
    private String className;
    private String requestingInstructorName;
    private String originalRoomName;
    private String temporaryRoomName;
    private RequestStatus status;
    private LocalDate effectiveDate;
    private OffsetDateTime requestedAt;
    private String description;
}