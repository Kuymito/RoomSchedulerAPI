// FILE: org/example/mybatisdemo/model/Notification.java
package org.example.roomschedulerapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.Timestamp;

public class Notification {
     @Schema(description = "Unique identifier of the Notification", example = "1")
     private Integer notificationId;

     @Schema(description = "User ID to whom this notification is sent", example = "101", required = true)
     private Integer userId; // FK

     @Schema(description = "Request ID associated with this notification", example = "1", required = true)
     private Integer requestId; // FK

     @Schema(description = "Content of the notification message", example = "Your room change request has been approved.", required = true)
     private String message;

     @Schema(description = "Status of the notification (e.g., unread, read)", example = "unread", required = true)
     private String status; // DEFAULT 'unread'

     @Schema(description = "Timestamp of when the notification was created", example = "2023-01-15T12:00:00Z", accessMode = Schema.AccessMode.READ_ONLY)
     private Timestamp createdAt; // DEFAULT CURRENT_TIMESTAMP

     public Notification() {
     }

     // Constructor, Getters, and Setters
     public Integer getNotificationId() { return notificationId; }
     public void setNotificationId(Integer notificationId) { this.notificationId = notificationId; }
     public Integer getUserId() { return userId; }
     public void setUserId(Integer userId) { this.userId = userId; }
     public Integer getRequestId() { return requestId; }
     public void setRequestId(Integer requestId) { this.requestId = requestId; }
     public String getMessage() { return message; }
     public void setMessage(String message) { this.message = message; }
     public String getStatus() { return status; }
     public void setStatus(String status) { this.status = status; }
     public Timestamp getCreatedAt() { return createdAt; }
     public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}