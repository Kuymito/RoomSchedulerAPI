// FILE: org/example/mybatisdemo/model/InstructorDepartment.java
package org.example.roomschedulerapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class InstructorDepartment {
    @Schema(description = "Instructor ID", example = "1", required = true)
    private Integer instructorId; // PK, FK

    @Schema(description = "Department ID", example = "10", required = true)
    private Integer departmentId; // PK, FK

    public InstructorDepartment() {
    }

    public InstructorDepartment(Integer instructorId, Integer departmentId) {
        this.instructorId = instructorId;
        this.departmentId = departmentId;
    }

    // Getters and Setters
    public Integer getInstructorId() { return instructorId; }
    public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
}