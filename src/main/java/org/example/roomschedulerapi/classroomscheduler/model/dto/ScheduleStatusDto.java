package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.ChangeRequest;
import org.example.roomschedulerapi.classroomscheduler.model.Schedule;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleStatusDto {
    private Long scheduleId;
    private String className;
    private Long roomId;
    private String roomName;
    private RequestStatus status;  // Can be null if no change request exists
    private Long temporaryRoomId;   // Can be null
    private String temporaryRoomName; // Can be null

    // Constructor for schedules without change requests
    public ScheduleStatusDto(Long scheduleId, String className, Long roomId, String roomName) {
        this(scheduleId, className, roomId, roomName, null, null, null);
    }

    // Constructor that takes Schedule and ChangeRequest entities
    public ScheduleStatusDto(Schedule schedule, ChangeRequest changeRequest) {
        this.scheduleId = schedule.getScheduleId();
        this.className = schedule.getAClass().getClassName();
        this.roomId = schedule.getRoom().getRoomId();
        this.roomName = schedule.getRoom().getRoomName();

        if (changeRequest != null) {
            this.status = changeRequest.getStatus();
            this.temporaryRoomId = changeRequest.getTemporaryRoom().getRoomId();
            this.temporaryRoomName = changeRequest.getTemporaryRoom().getRoomName();
        } else {
            this.status = null;
            this.temporaryRoomId = null;
            this.temporaryRoomName = null;
        }
    }

}