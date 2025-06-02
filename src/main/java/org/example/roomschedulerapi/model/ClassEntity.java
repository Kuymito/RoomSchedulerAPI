// FILE: org/example/mybatisdemo/model/AcademicClass.java
package org.example.roomschedulerapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.Date; // Use java.sql.Date for SQL DATE type

public class ClassEntity {
    @Schema(description = "Unique identifier of the Class", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer classId;

    @Schema(description = "Instructor ID for this class", example = "1", required = true)
    private Integer instructorId; // FK

    @Schema(description = "Course ID for this class", example = "101", required = true)
    private Integer courseId; // FK

    @Schema(description = "Room ID where this class is held", example = "201", required = true)
    private Integer roomId; // FK

    @Schema(description = "Timeslot ID for this class", example = "301", required = true)
    private Integer timeslotId; // FK

    @Schema(description = "Maximum number of students allowed", example = "25", required = true)
    private Integer maxStudents;

    @Schema(description = "Start date of the class", example = "2024-09-01", required = true, type = "string", format = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;

    @Schema(description = "End date of the class", example = "2024-12-15", required = true, type = "string", format = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;

    @Schema(description = "Status of the class (e.g., Scheduled, Ongoing, Completed)", example = "Scheduled", required = true)
    private String status;

    @Schema(description = "Indicates if the class is archived", example = "false", nullable = true)
    private Boolean isArchived; // Boolean (wrapper) because it can be null in DB and allows checking for presence in PATCH

    public ClassEntity() {
    }

    // Constructor, Getters, and Setters
    public Integer getClassId() { return classId; }
    public void setClassId(Integer classId) { this.classId = classId; }
    public Integer getInstructorId() { return instructorId; }
    public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public Integer getTimeslotId() { return timeslotId; }
    public void setTimeslotId(Integer timeslotId) { this.timeslotId = timeslotId; }
    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getIsArchived() { return isArchived; } // Getter for Boolean
    public void setIsArchived(Boolean archived) { isArchived = archived; } // Setter for Boolean

    public ClassEntity orElse(Object o) {
        return (ClassEntity) o;
    }
}