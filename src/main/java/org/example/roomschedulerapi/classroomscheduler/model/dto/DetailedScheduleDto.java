package org.example.roomschedulerapi.classroomscheduler.model.dto;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalTime; // Assuming LocalTime for start/end times
@NoArgsConstructor

public class DetailedScheduleDto {
    private Long scheduleId;
    private Long classId;
    private String className;
    private String classCode;
    private Long roomId;
    private String roomName;
    private String buildingName;
    private Integer floorNumber;
    private Integer capacity;
    private Long shiftId;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
    private String day; // e.g., "Monday"
    private String groupName;
    private String semester;
    private String year;
    private String degree;
    private String generation;
    private Long instructorId;
    private String instructorName;
    private String instructorEmail;
    private String instructorPhone;
    private String instructorProfile;
    private String instructorMajor;
    private String instructorDegree;
    private String departmentName;
    private Boolean instructorArchived;
    private Boolean classIsOnline;
    private Boolean classArchived;

    // Constructor matching the @Query selection order and types
    public DetailedScheduleDto(
            Long scheduleId, Long classId, String className, String classCode,
            Long roomId, String roomName, String buildingName, Integer floorNumber, Integer capacity,
            Long shiftId, String shiftName, LocalTime startTime, LocalTime endTime, String day,
            String groupName, String semester, String year, String degree, String generation,
            Long instructorId, String instructorName, String instructorEmail, String instructorPhone,
            String instructorProfile, String instructorMajor, String instructorDegree, String departmentName,
            Boolean instructorArchived, Boolean classIsOnline, Boolean classArchived) {
        this.scheduleId = scheduleId;
        this.classId = classId;
        this.className = className;
        this.classCode = classCode;
        this.roomId = roomId;
        this.roomName = roomName;
        this.buildingName = buildingName;
        this.floorNumber = floorNumber;
        this.capacity = capacity;
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.groupName = groupName;
        this.semester = semester;
        this.year = year;
        this.degree = degree;
        this.generation = generation;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.instructorEmail = instructorEmail;
        this.instructorPhone = instructorPhone;
        this.instructorProfile = instructorProfile;
        this.instructorMajor = instructorMajor;
        this.instructorDegree = instructorDegree;
        this.departmentName = departmentName;
        this.instructorArchived = instructorArchived;
        this.classIsOnline = classIsOnline;
        this.classArchived = classArchived;
    }

    // Getters and setters (omitted for brevity)
    // ...
}