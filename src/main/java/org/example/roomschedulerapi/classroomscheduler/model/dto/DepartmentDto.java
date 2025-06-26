package org.example.roomschedulerapi.classroomscheduler.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class DepartmentDto {
    private Long departmentId;

    @NotBlank(message = "Department name cannot be blank")
    private String name;

    // Constructor to easily convert from an Entity to a DTO
    public DepartmentDto(Long departmentId, String name) {
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