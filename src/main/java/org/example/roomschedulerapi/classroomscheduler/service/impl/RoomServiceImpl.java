package org.example.roomschedulerapi.classroomscheduler.service.impl;

import org.example.roomschedulerapi.classroomscheduler.model.Room;
import org.example.roomschedulerapi.classroomscheduler.model.dto.RoomScheduleDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.RoomUpdateDto;
import org.example.roomschedulerapi.classroomscheduler.repository.RoomRepository;
import org.example.roomschedulerapi.classroomscheduler.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomScheduleDto> getAllRooms() {
        // --- UPDATE THIS METHOD'S LOGIC ---
        return roomRepository.findAll().stream()
                .map(this::mapToRoomScheduleDto) // Reuse the mapping logic
                .collect(Collectors.toList());
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

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomScheduleDto> getRoomSchedule(Long roomId) {
        return roomRepository.findById(roomId).map(this::mapToRoomScheduleDto);
    }

    private RoomScheduleDto mapToRoomScheduleDto(Room room) {
        RoomScheduleDto dto = new RoomScheduleDto();
        dto.setRoomId(room.getRoomId());
        dto.setRoomName(room.getRoomName());
        dto.setBuildingName(room.getBuildingName());
        dto.setFloor(room.getFloor());
        dto.setCapacity(room.getCapacity());
        dto.setType(room.getType());
        dto.setEquipment(room.getEquipment());

        List<RoomScheduleDto.DailyAvailabilityDto> dailyDtos = room.getAvailability()
                .stream()
                .map(avail -> {
                    RoomScheduleDto.DailyAvailabilityDto dailyDto = new RoomScheduleDto.DailyAvailabilityDto();
                    dailyDto.setDayOfWeek(avail.getDayOfWeek());
                    dailyDto.setAvailable(avail.getIsAvailable());
                    return dailyDto;
                })
                .collect(Collectors.toList());

        dto.setSchedule(dailyDtos);
        return dto;
    }
}