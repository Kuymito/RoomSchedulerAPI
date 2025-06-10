package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentResponseDto {
    private Long departmentId;
    private String name;

    public DepartmentResponseDto(Long departmentId, String name) {
        this.departmentId = departmentId;
        this.name = name;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}