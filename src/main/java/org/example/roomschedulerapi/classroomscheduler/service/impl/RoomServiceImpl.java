package org.example.roomschedulerapi.classroomscheduler.service.impl;

import org.example.roomschedulerapi.classroomscheduler.model.Room;
import org.example.roomschedulerapi.classroomscheduler.model.dto.RoomUpdateDto;
import org.example.roomschedulerapi.classroomscheduler.repository.RoomRepository;
import org.example.roomschedulerapi.classroomscheduler.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return roomRepository.findById(roomId);
    }

    @Override
    @Transactional
    public Room patchRoom(Long roomId, RoomUpdateDto dto) {
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("Room not found with id: " + roomId));

        if (dto.getRoomName() != null) existingRoom.setRoomName(dto.getRoomName());
        if (dto.getBuildingName() != null) existingRoom.setBuildingName(dto.getBuildingName());
        if (dto.getFloor() != null) existingRoom.setFloor(dto.getFloor());
        if (dto.getCapacity() != null) existingRoom.setCapacity(dto.getCapacity());
        if (dto.getType() != null) existingRoom.setType(dto.getType());
        if (dto.getEquipment() != null) existingRoom.setEquipment(dto.getEquipment());
        if (dto.getIsAvailable() != null) existingRoom.setIsAvailable(dto.getIsAvailable());

        return roomRepository.save(existingRoom);
    }

    @Override
    @Transactional
    public void deleteRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new NoSuchElementException("Cannot delete. Room not found with id: " + roomId);
        }
        roomRepository.deleteById(roomId);
    }
}