package org.example.roomschedulerapi.classroomscheduler.repository;

import org.example.roomschedulerapi.classroomscheduler.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // Method to check if a Class is already in any schedule
    boolean existsByaClass_ClassId(Long classId);
}