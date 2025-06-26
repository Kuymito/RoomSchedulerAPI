package org.example.roomschedulerapi.classroomscheduler.repository; // Adjust to your package

import org.example.roomschedulerapi.classroomscheduler.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find a role by its name (useful for checking uniqueness before creation/update)
    Optional<Role> findByRoleName(String roleName);
}