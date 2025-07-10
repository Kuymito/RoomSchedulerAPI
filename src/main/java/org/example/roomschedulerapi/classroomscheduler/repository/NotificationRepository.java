package org.example.roomschedulerapi.classroomscheduler.repository;

import org.example.roomschedulerapi.classroomscheduler.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByAdmin_AdminIdOrderByCreatedAtDesc(Integer adminId);

    List<Notification> findByInstructor_InstructorIdOrderByCreatedAtDesc(Long instructorId);
}