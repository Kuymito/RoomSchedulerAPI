// FILE: org/example/mybatisdemo/model/Course.java
package org.example.roomschedulerapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class Course {
    @Schema(description = "Unique identifier of the Course", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer courseId;

    @Schema(description = "Department ID to which this course belongs", example = "10", required = true)
    private Integer departmentId; // FK to department(department_id)

    @Schema(description = "Name of the course", example = "Introduction to Programming", required = true)
    private String name; // Following SQL schema

    @Schema(description = "Unique code for the course", example = "CS101", required = true)
    private String code; // Following SQL schema

    @Schema(description = "Credit hours for the course", example = "3", required = true)
    private Integer creditHours; // Following SQL schema

    public Course() {
    }

    public Course(Integer courseId, Integer departmentId, String name, String code, Integer creditHours) {
        this.courseId = courseId;
        this.departmentId = departmentId;
        this.name = name;
        this.code = code;
        this.creditHours = creditHours;
    }

    // Getters and Setters
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getCreditHours() { return creditHours; }
    public void setCreditHours(Integer creditHours) { this.creditHours = creditHours; }
}