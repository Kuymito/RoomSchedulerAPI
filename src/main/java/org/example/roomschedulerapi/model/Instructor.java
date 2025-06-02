// FILE: org/example/mybatisdemo/model/Instructor.java
package org.example.roomschedulerapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class Instructor {
    @Schema(description = "Unique identifier of the Instructor", example = "1")
    private Integer instructorId;

    @Schema(description = "User ID associated with this instructor", example = "101", required = true)
    private Integer userId; // FK to users(user_id)

    @Schema(description = "Full name of the instructor", example = "Dr. Jane Smith", required = true)
    private String name;

    @Schema(description = "Phone number of the instructor", example = "555-0101", required = true)
    private String phone;

    @Schema(description = "Degree held by the instructor", example = "Ph.D. in Computer Science", required = true)
    private String degree;

    @Schema(description = "Address of the instructor", example = "123 Education Lane, University City", required = true)
    private String address;

    public Instructor() {
    }

    // Constructor, Getters, and Setters
    public Integer getInstructorId() { return instructorId; }
    public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}