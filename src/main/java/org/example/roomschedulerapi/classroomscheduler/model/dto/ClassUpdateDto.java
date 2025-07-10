package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassUpdateDto {
    private String className;
    private String generation;
    private String groupName;
    private String major;
    private String degree;
    private String semester;
    private String day;
    private Integer year;
    private Long departmentId;
    private Long shiftId;
    private Boolean isArchived;
}