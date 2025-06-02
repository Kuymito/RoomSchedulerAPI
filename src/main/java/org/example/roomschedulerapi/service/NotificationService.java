// FILE: org/example/mybatisdemo/service/NotificationService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Notification;
import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications(); // Admin use
    Notification getNotificationById(Integer notificationId);
    List<Notification> getNotificationsByUserId(Integer userId);
    List<Notification> getNotificationsByUserIdAndStatus(Integer userId, String status);
    Notification createNotification(Notification notification); // Usually internal or admin
    Notification updateNotificationStatus(Integer notificationId, String status);
    void deleteNotification(Integer notificationId);
    void markAllAsReadForUser(Integer userId);
}