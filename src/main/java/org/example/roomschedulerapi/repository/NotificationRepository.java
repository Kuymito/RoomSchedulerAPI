// FILE: org/example/mybatisdemo/repository/NotificationRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.Notification;
import java.util.List;

@Mapper
public interface NotificationRepository {
    @Select("SELECT * FROM notifications")
    List<Notification> findAll();

    @Select("SELECT * FROM notifications WHERE notification_id = #{notificationId}")
    Notification findById(Integer notificationId);

    // status and created_at have defaults
    @Insert("INSERT INTO notifications(user_id, request_id, message, status) " +
            "VALUES(#{userId}, #{requestId}, #{message}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "notificationId", keyColumn = "notification_id")
    void insert(Notification notification);

    @Update("UPDATE notifications SET user_id=#{userId}, request_id=#{requestId}, message=#{message}, status=#{status} " +
            "WHERE notification_id=#{notificationId}")
    void update(Notification notification);

    @Update("UPDATE notifications SET status=#{status} WHERE notification_id=#{notificationId}")
    void updateStatus(@Param("notificationId") Integer notificationId, @Param("status") String status);


    @Delete("DELETE FROM notifications WHERE notification_id=#{notificationId}")
    void delete(Integer notificationId);

    @Select("SELECT * FROM notifications WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Notification> findByUserId(Integer userId);

    List<Notification> findByUserIdAndStatus(Integer userId, String status);

    void markAllAsReadForUser(Integer userId);
}