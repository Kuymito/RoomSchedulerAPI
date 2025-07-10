package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ClassResponseDto {
    private Long classId;
    private String className;
    private String generation;
    private String groupName;
    private String majorName;
    private String degreeName;
    private String semester;
    private boolean isOnline; // General flag for the class
    private boolean isFree;
    private boolean isArchived;
    private LocalDateTime createdAt;
    private LocalDateTime archivedAt;
    private DepartmentResponseDto department;
    private ShiftResponseDto shift;

    // This map now holds the full details for each scheduled day
    private Map<String, ClassDayDetailsDto> dailySchedule;

    public ClassResponseDto(Long classId, String className, String generation, String groupName, String majorName,
                            String degreeName, String semester, boolean isOnline, boolean isFree, boolean isArchived,
                            LocalDateTime createdAt, LocalDateTime archivedAt, Map<String, ClassDayDetailsDto> dailySchedule,
                            DepartmentResponseDto department, ShiftResponseDto shift) {
        this.classId = classId;
        this.className = className;
        this.generation = generation;
        this.groupName = groupName;
        this.majorName = majorName;
        this.degreeName = degreeName;
        this.semester = semester;
        this.isOnline = isOnline;
        this.isFree = isFree;
        this.isArchived = isArchived;
        this.createdAt = createdAt;
        this.archivedAt = archivedAt;
        this.dailySchedule = dailySchedule;
        this.department = department;
        this.shift = shift;
    }
}