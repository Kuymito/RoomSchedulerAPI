package org.example.roomschedulerapi.classroomscheduler.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.*;
import org.example.roomschedulerapi.classroomscheduler.model.dto.NotificationResponseDto;
import org.example.roomschedulerapi.classroomscheduler.repository.AdminRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.InstructorRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.NotificationRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.RoomRepository;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final NotificationLock notificationLock;

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
        // Step 1: Attempt to acquire a lock for this specific change request.
        if (!notificationLock.acquire(changeRequest.getId())) {
            // If we fail to get the lock, it means another process is already creating
            // the notification. We can safely exit.
            System.out.println("Duplicate notification creation blocked by lock for request ID: " + changeRequest.getId());
            return;
        }

        try {
            // Step 2: If the lock is acquired, check if the notification already exists.
            // This handles cases where a previous, completed process already created it.
            boolean notificationExists = notificationRepository.existsByChangeRequestIdAndMessage(changeRequest.getId(), message);
            if (notificationExists) {
                return; // Exit if the notification is already in the database.
            }

            // Step 3: If no notification exists, create and save the new one.
            Instructor instructor = instructorRepository.findById(instructorId)
                    .orElseThrow(() -> new NoSuchElementException("Cannot create notification for non-existent instructor with id: " + instructorId));

            Notification notification = new Notification();
            notification.setInstructor(instructor);
            notification.setChangeRequest(changeRequest);
            notification.setMessage(message);

            notificationRepository.save(notification);

        } finally {
            // Step 4: CRITICAL - Always release the lock, even if an error occurs.
            notificationLock.release(changeRequest.getId());
        }
    }

    public List<NotificationResponseDto> getNotificationsForUser(UserDetails userDetails) {
        List<Notification> notifications;

        if (userDetails instanceof Admin admin) {
            // No more need for Math.toIntExact()
            notifications = notificationRepository.findByAdmin_AdminIdOrderByCreatedAtDesc((long) admin.getAdminId());
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

        ChangeRequest changeRequest = notification.getChangeRequest();
        if (changeRequest != null) {
            dto.setStatus(changeRequest.getStatus());
            dto.setDescription(changeRequest.getDescription());
            // --- THIS IS THE FIX ---
            // If the notification is for an admin, populate the instructor's profile.
            // Use the correct getter 'getRequestingInstructor()' generated by Lombok.
            if (notification.getAdmin() != null && changeRequest.getRequestingInstructor() != null) {
                Instructor instructor = changeRequest.getRequestingInstructor();
                String profileName = instructor.getProfile();
                dto.setProfile(profileName);
            }
        }

        return dto;
    }
}