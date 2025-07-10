package org.example.roomschedulerapi.classroomscheduler.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.*;
import org.example.roomschedulerapi.classroomscheduler.model.dto.NotificationResponseDto;
import org.example.roomschedulerapi.classroomscheduler.repository.AdminRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.NotificationRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AdminRepository adminRepository;

    /**
     * Creates a notification for all admins.
     */
    public void createNotificationForAdmins(ChangeRequest changeRequest, String message) {
        List<Admin> admins = adminRepository.findAll();
        for (Admin admin : admins) {
            Notification notification = new Notification();
            notification.setAdmin(admin);
            notification.setChangeRequest(changeRequest);
            notification.setMessage(message);
            notification.setCreatedAt(OffsetDateTime.now());
            notificationRepository.save(notification);
        }
    }

    /**
     * Creates a notification for a specific instructor.
     */
    public void createNotificationForInstructor(Instructor instructor, ChangeRequest changeRequest, String message) {
        Notification notification = new Notification();
        notification.setInstructor(instructor);
        notification.setChangeRequest(changeRequest);
        notification.setMessage(message);
        notification.setCreatedAt(OffsetDateTime.now());
        notificationRepository.save(notification);
    }

    public List<NotificationResponseDto> getNotificationsForUser(UserDetails userDetails) {
        List<Notification> notifications;

        if (userDetails instanceof Admin) {
            Admin admin = (Admin) userDetails;
            notifications = notificationRepository.findByAdmin_AdminIdOrderByCreatedAtDesc(Math.toIntExact(admin.getAdminId()));
        } else if (userDetails instanceof Instructor) {
            Instructor instructor = (Instructor) userDetails;
            notifications = notificationRepository.findByInstructor_InstructorIdOrderByCreatedAtDesc(instructor.getInstructorId());
        } else {
            return Collections.emptyList();
        }

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Marks a specific notification as read.
     */
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Notification not found with ID: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponseDto convertToDto(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();

        dto.setNotificationId(notification.getNotificationId());
        dto.setChangeRequestId(notification.getChangeRequest().getRequestId());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());

        // --- THIS IS THE FIX ---
        // Get the status from the associated ChangeRequest and set it on the DTO.
        if (notification.getChangeRequest() != null) {
            dto.setStatus(notification.getChangeRequest().getStatus());
        }

        return dto;
    }
}