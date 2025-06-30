package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class ShiftResponseDto {
    private Long shiftId;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private String scheduleType;

    public ShiftResponseDto(Long shiftId, String name, LocalTime startTime, LocalTime endTime, String scheduleType) {
        this.shiftId = shiftId;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduleType = scheduleType;
    }

    public ShiftResponseDto(){}

    public ShiftResponseDto(Long shiftId, LocalTime startTime, LocalTime endTime) {
        this.shiftId = shiftId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }
}