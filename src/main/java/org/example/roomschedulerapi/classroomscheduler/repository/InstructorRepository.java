package org.example.roomschedulerapi.classroomscheduler.repository; // Adjust to your package

import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    // Custom query to find instructors by archived status
    List<Instructor> findByIsArchived(boolean isArchived);

    // Check if an email already exists (useful for validation)
    Optional<Instructor> findByEmail(String email);
}