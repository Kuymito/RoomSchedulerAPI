// FILE: org/example/mybatisdemo/model/Department.java
package org.example.roomschedulerapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class Department {
    @Schema(description = "Unique identifier of the Department", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer departmentId;

    @Schema(description = "Name of the department", example = "Computer Science", required = true)
    private String name;

    @Schema(description = "Short code for the department", example = "CS", required = true)
    private String code;

    public Department() {
    }

    public Department(Integer departmentId, String name, String code) {
        this.departmentId = departmentId;
        this.name = name;
        this.code = code;
    }

    // Getters and Setters
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}