package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.*;

import java.time.LocalTime;

@Data
@RequiredArgsConstructor
public class ShiftResponseDto {
    private Long shiftId;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private String scheduleType;

    public ShiftResponseDto(Long shiftId, LocalTime startTime, LocalTime endTime, String name) {
        this.shiftId = shiftId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
    }

    public ShiftResponseDto(Long shiftId, LocalTime startTime, LocalTime endTime) {
        this.shiftId = shiftId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}