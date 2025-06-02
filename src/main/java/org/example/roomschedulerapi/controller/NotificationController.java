// FILE: org/example/mybatisdemo/controller/NotificationController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.Notification;
import org.example.roomschedulerapi.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification Controller", description = "APIs for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications"),
            @ApiResponse(responseCode = "404", description = "User not found or no notifications")
    })
    public ResponseEntity<List<Notification>> getNotificationsByUserId(
            @Parameter(description = "ID of the user") @PathVariable Integer userId,
            @Parameter(description = "Filter by status (e.g., 'unread', 'read')", required = false) @RequestParam(required = false) String status) {
        List<Notification> notifications;
        if (status != null && !status.isEmpty()) {
            notifications = notificationService.getNotificationsByUserIdAndStatus(userId, status);
        } else {
            notifications = notificationService.getNotificationsByUserId(userId);
        }
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notification"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<Notification> getNotificationById(@Parameter(description = "ID of the notification") @PathVariable Integer id) {
        Notification notification = notificationService.getNotificationById(id);
        return (notification != null) ? ResponseEntity.ok(notification) : ResponseEntity.notFound().build();
    }

    // Create is usually an internal operation triggered by other events (e.g., request status change)
    // So a public POST endpoint might not be typical unless admins can send manual notifications.
    // For this example, let's assume it's possible for an admin to create one.
    @PostMapping
    @Operation(summary = "Create a new notification (typically for admin use)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created notification"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., user or request not found)")
    })
    public ResponseEntity<Notification> createNotification(@Parameter(description = "Notification object to be created") @RequestBody Notification notification) {
        // Ensure 'status' and 'createdAt' are handled (defaults in DB or service)
        if (notification.getStatus() == null) {
            notification.setStatus("unread"); // Default status
        }
        Notification createdNotification = notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }


    @PatchMapping("/{id}/status")
    @Operation(summary = "Update the status of a notification (e.g., mark as read)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated notification status"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<Notification> updateNotificationStatus(
            @Parameter(description = "ID of the notification to update") @PathVariable Integer id,
            @Parameter(description = "New status for the notification", example = "{\"status\":\"read\"}") @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        if (status == null || (!status.equalsIgnoreCase("read") && !status.equalsIgnoreCase("unread"))) {
            return ResponseEntity.badRequest().build(); // Or throw custom exception
        }
        Notification updatedNotification = notificationService.updateNotificationStatus(id, status);
        return (updatedNotification != null) ? ResponseEntity.ok(updatedNotification) : ResponseEntity.notFound().build();
    }

    @PostMapping("/user/{userId}/mark-all-read")
    @Operation(summary = "Mark all unread notifications as read for a specific user")
    @ApiResponse(responseCode = "204", description = "Successfully marked all notifications as read")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable Integer userId) {
        notificationService.markAllAsReadForUser(userId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted notification"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<Void> deleteNotification(@Parameter(description = "ID of the notification to delete") @PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}