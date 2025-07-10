package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Data;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;

import java.time.OffsetDateTime;

@Data
public class NotificationResponseDto {

    private Long notificationId;
    private Long changeRequestId;
    private String message;
    private boolean isRead;
    private OffsetDateTime createdAt;
    private RequestStatus status;
}