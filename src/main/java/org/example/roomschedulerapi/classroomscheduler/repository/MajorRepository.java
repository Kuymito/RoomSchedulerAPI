package org.example.roomschedulerapi.classroomscheduler.repository;

import org.example.roomschedulerapi.classroomscheduler.model.Department;
import org.example.roomschedulerapi.classroomscheduler.model.Major;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRepository extends JpaRepository<Major, Long> {
}
