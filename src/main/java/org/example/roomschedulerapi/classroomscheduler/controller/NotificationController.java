package org.example.roomschedulerapi.classroomscheduler.controller;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.dto.NotificationResponseDto;
import org.example.roomschedulerapi.classroomscheduler.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUserNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<NotificationResponseDto> notifications = notificationService.getNotificationsForUser(userDetails);
        return ResponseEntity.ok(new ApiResponse<>(
                "Notifications retrieved successfully", notifications, HttpStatus.OK, LocalDateTime.now()));
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok(new ApiResponse<>(
                "Notification marked as read", null, HttpStatus.OK, LocalDateTime.now()));
    }
}