package org.example.roomschedulerapi.classroomscheduler.repository;

import org.example.roomschedulerapi.classroomscheduler.model.ClassInstructor;
import org.example.roomschedulerapi.classroomscheduler.model.DaysOfWeek;
import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassInstructorRepository extends JpaRepository<ClassInstructor, Long> {
    List<ClassInstructor> findByInstructor(Instructor instructor);
    Optional<ClassInstructor> findByaClass_ClassIdAndDayOfWeek(Long classId, DaysOfWeek dayOfWeek);
}