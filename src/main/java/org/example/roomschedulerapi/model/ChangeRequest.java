// FILE: org/example/mybatisdemo/model/ChangeRequest.java
package org.example.roomschedulerapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.Timestamp;

public class ChangeRequest {
     @Schema(description = "Unique identifier of the Change Request", example = "1")
     private Integer requestId;

     @Schema(description = "Instructor ID making the request", example = "1", required = true)
     private Integer instructorId; // FK

     @Schema(description = "Class ID for which the change is requested", example = "101", required = true)
     private Integer classId; // FK

     @Schema(description = "Reason for the change request", example = "Room conflict")
     private String reason;

     @Schema(description = "Preferred room ID for the class", example = "205", required = true)
     private Integer preferredRoomId; // FK to rooms(room_id), name in SQL is preferred_room

     @Schema(description = "Status of the request (e.g., pending, approved, rejected)", example = "pending", required = true)
     private String status; // DEFAULT 'pending'

     @Schema(description = "Timestamp of when the request was made", example = "2023-01-15T11:00:00Z", accessMode = Schema.AccessMode.READ_ONLY)
     private Timestamp requestedAt; // DEFAULT CURRENT_TIMESTAMP

     public ChangeRequest() {
     }

     // Constructor, Getters, and Setters
     public Integer getRequestId() { return requestId; }
     public void setRequestId(Integer requestId) { this.requestId = requestId; }
     public Integer getInstructorId() { return instructorId; }
     public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }
     public Integer getClassId() { return classId; }
     public void setClassId(Integer classId) { this.classId = classId; }
     public String getReason() { return reason; }
     public void setReason(String reason) { this.reason = reason; }
     public Integer getPreferredRoomId() { return preferredRoomId; }
     public void setPreferredRoomId(Integer preferredRoomId) { this.preferredRoomId = preferredRoomId; }
     public String getStatus() { return status; }
     public void setStatus(String status) { this.status = status; }
     public Timestamp getRequestedAt() { return requestedAt; }
     public void setRequestedAt(Timestamp requestedAt) { this.requestedAt = requestedAt; }
}