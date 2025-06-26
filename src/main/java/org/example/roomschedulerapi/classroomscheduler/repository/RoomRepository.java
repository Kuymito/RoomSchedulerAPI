package org.example.roomschedulerapi.classroomscheduler.repository;

import org.example.roomschedulerapi.classroomscheduler.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> { // Assuming Long is the type of roomId
    // Spring Data JPA automatically provides methods like:
    // - save(Room room) -> for creating and updating
    // - findById(Long roomId) -> for fetching a room by its ID
    // - findAll() -> for fetching all rooms
    // - deleteById(Long roomId) -> for deleting a room
    // - existsById(Long roomId)
    // ...and more.

    // You can add custom query methods here if needed, e.g.:
    // List<Room> findByBuildingName(String buildingName);
}