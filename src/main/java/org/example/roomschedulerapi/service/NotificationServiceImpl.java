// FILE: org/example/mybatisdemo/service/NotificationServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Notification;
import org.example.roomschedulerapi.repository.NotificationRepository;
// import org.example.mybatisdemo.exception.ResourceNotFoundException;
// import org.example.mybatisdemo.repository.UserRepo; // For validation
// import org.example.mybatisdemo.repository.ChangeRequestRepo; // For validation
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    // Inject UserRepo, ChangeRequestRepo if FK validation is needed

    public NotificationServiceImpl(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepo.findAll();
    }

    @Override
    public Notification getNotificationById(Integer notificationId) {
        Notification notification = notificationRepo.findById(notificationId);
        // if (notification == null) {
        //     throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
        // }
        return notification;
    }

    @Override
    public List<Notification> getNotificationsByUserId(Integer userId) {
        return notificationRepo.findByUserId(userId);
    }

    @Override
    public List<Notification> getNotificationsByUserIdAndStatus(Integer userId, String status) {
        return notificationRepo.findByUserIdAndStatus(userId, status);
    }

    @Override
    public Notification createNotification(Notification notification) {
        // Validate FKs: userId, requestId
        if (notification.getStatus() == null || notification.getStatus().isEmpty()) {
            notification.setStatus("unread");
        }
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(Timestamp.from(Instant.now()));
        }
        notificationRepo.insert(notification);
        return notification;
    }

    @Override
    public Notification updateNotificationStatus(Integer notificationId, String status) {
        Notification existingNotification = notificationRepo.findById(notificationId);
        if (existingNotification == null) {
            // throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
            return null;
        }
        if (status == null || (!status.equalsIgnoreCase("read") && !status.equalsIgnoreCase("unread"))) {
            throw new IllegalArgumentException("Invalid status value. Must be 'read' or 'unread'.");
        }
        notificationRepo.updateStatus(notificationId, status);
        existingNotification.setStatus(status); // Update object in memory
        return existingNotification;
    }

    @Override
    public void markAllAsReadForUser(Integer userId) {
        // Optional: check if user exists
        notificationRepo.markAllAsReadForUser(userId);
    }

    @Override
    public void deleteNotification(Integer notificationId) {
        Notification existingNotification = notificationRepo.findById(notificationId);
        if (existingNotification == null) {
            // throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
            return;
        }
        notificationRepo.delete(notificationId);
    }
}