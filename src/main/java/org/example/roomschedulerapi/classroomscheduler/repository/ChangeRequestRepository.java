package org.example.roomschedulerapi.classroomscheduler.repository;

import org.example.roomschedulerapi.classroomscheduler.model.ChangeRequest; // Assuming you have this model
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long> {

    /**
     * Counts all change requests that are marked as expired.
     * @param expired The expired status (true).
     * @return The total count of expired requests.
     */
    long countByExpired(boolean expired);
}