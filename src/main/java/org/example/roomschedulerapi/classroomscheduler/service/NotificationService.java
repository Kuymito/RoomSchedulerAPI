package org.example.roomschedulerapi.classroomscheduler.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.*;
import org.example.roomschedulerapi.classroomscheduler.model.dto.NotificationResponseDto;
import org.example.roomschedulerapi.classroomscheduler.repository.AdminRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.InstructorRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.NotificationRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.RoomRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final InstructorRepository instructorRepository;

    /**
     * Creates a notification for all admins.
     */
    public void createNotificationForAdmins(ChangeRequest changeRequest, String message) {
        List<Admin> admins = adminRepository.findAll();
        if (admins.isEmpty()) return;

        List<Notification> notificationsToSave = admins.stream().map(admin -> {
            Notification notification = new Notification();
            notification.setAdmin(admin);
            notification.setChangeRequest(changeRequest);
            notification.setMessage(message);
            return notification;
        }).collect(Collectors.toList());
        notificationRepository.saveAll(notificationsToSave);
    }

    public void createNotificationForInstructor(Long instructorId, ChangeRequest changeRequest, String message) {
        // FIX: Fetch the instructor from the database to ensure it is a managed entity.
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new NoSuchElementException("Cannot create notification for non-existent instructor with id: " + instructorId));

        Notification notification = new Notification();
        notification.setInstructor(instructor); // Use the managed instructor object
        notification.setChangeRequest(changeRequest);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public List<NotificationResponseDto> getNotificationsForUser(UserDetails userDetails) {
        List<Notification> notifications;

        if (userDetails instanceof Admin admin) {
            // No more need for Math.toIntExact()
            notifications = notificationRepository.findByAdmin_AdminIdOrderByCreatedAtDesc(admin.getAdminId());
        } else if (userDetails instanceof Instructor instructor) {
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

        dto.setNotificationId(notification.getId());
        dto.setChangeRequestId(notification.getChangeRequest().getId());
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