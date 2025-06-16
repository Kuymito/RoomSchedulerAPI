package org.example.roomschedulerapi.classroomscheduler.repository;

import org.example.roomschedulerapi.classroomscheduler.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Method to find an admin by their email address
    Optional<Admin> findByEmail(String email);
}