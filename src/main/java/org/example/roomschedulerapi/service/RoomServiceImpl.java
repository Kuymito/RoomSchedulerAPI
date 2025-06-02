// FILE: org/example/mybatisdemo/service/RoomServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Room;
import org.example.roomschedulerapi.repository.RoomRepository;
// import org.example.mybatisdemo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepo;

    public RoomServiceImpl(RoomRepository roomRepo) {
        this.roomRepo = roomRepo;
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepo.findAll();
    }

    @Override
    public Room getRoomById(Integer roomId) {
        Room room = roomRepo.findById(roomId);
        // if (room == null) {
        //     throw new ResourceNotFoundException("Room not found with id: " + roomId);
        // }
        return room;
    }

    @Override
    public List<Room> getRoomsByBuildingId(Integer buildingId) {
        return roomRepo.findByBuildingId(buildingId);
    }

    @Override
    public Room createRoom(Room room) {
        // Add validation (e.g., building_id exists, unique number within building)
        roomRepo.insert(room);
        return room;
    }

    @Override
    public Room updateRoom(Integer roomId, Room roomDetails) {
        Room existingRoom = roomRepo.findById(roomId);
        if (existingRoom == null) {
            // throw new ResourceNotFoundException("Room not found with id: " + roomId);
            return null;
        }
        roomDetails.setRoomId(roomId);
        roomRepo.update(roomDetails);
        return roomDetails;
    }

    @Override
    public Room partialUpdateRoom(Integer roomId, Room roomDetails) {
        Room existingRoom = roomRepo.findById(roomId);
        if (existingRoom == null) {
            // throw new ResourceNotFoundException("Room not found with id: " + roomId);
            return null;
        }

        boolean updated = false;
        if (roomDetails.getBuildingId() != null) {
            existingRoom.setBuildingId(roomDetails.getBuildingId());
            updated = true;
        }
        if (roomDetails.getNumber() != null) {
            existingRoom.setNumber(roomDetails.getNumber());
            updated = true;
        }
        if (roomDetails.getCapacity() != null) {
            existingRoom.setCapacity(roomDetails.getCapacity());
            updated = true;
        }
        if (roomDetails.getType() != null) {
            existingRoom.setType(roomDetails.getType());
            updated = true;
        }

        if (updated) {
            roomRepo.update(existingRoom);
        }
        return existingRoom;
    }

    @Override
    public void deleteRoom(Integer roomId) {
        Room existingRoom = roomRepo.findById(roomId);
        if (existingRoom == null) {
            // throw new ResourceNotFoundException("Room not found with id: " + roomId);
            return;
        }
        // Consider referential integrity: what happens if classes or requests reference this room?
        roomRepo.delete(roomId);
    }
}