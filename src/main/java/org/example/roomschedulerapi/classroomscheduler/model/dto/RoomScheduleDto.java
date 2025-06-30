package org.example.roomschedulerapi.classroomscheduler.model.dto;

import jakarta.persistence.Column;
import lombok.Data;
import java.util.List;

@Data
public class RoomScheduleDto {
    private Long roomId;
    private String roomName;
    private String buildingName;
    private Integer floor;
    private Integer capacity;
    private String type;
    private String equipment;
    private List<DailyAvailabilityDto> schedule;

    @Data
    public static class DailyAvailabilityDto {
        private String dayOfWeek;
        private boolean isAvailable;
    }
}