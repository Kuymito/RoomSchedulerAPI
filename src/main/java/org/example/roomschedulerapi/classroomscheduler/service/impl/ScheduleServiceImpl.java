package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.model.Room;
import org.example.roomschedulerapi.classroomscheduler.model.Schedule;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleResponseDto;
import org.example.roomschedulerapi.classroomscheduler.repository.ClassRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.RoomRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.ScheduleRepository;
import org.example.roomschedulerapi.classroomscheduler.service.ScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;
    private final RoomRepository roomRepository;


    private ScheduleResponseDto convertToDto(Schedule schedule) {
        return new ScheduleResponseDto(
                schedule.getScheduleId(),
                schedule.getAClass().getClassId(),
                schedule.getAClass().getClassName(),
                schedule.getRoom().getRoomId(),
                schedule.getRoom().getRoomName(),
                schedule.getRoom().getBuildingName()
        );
    }

    @Override
    public List<ScheduleResponseDto> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ScheduleResponseDto> getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).map(this::convertToDto);
    }
    @Transactional
    @Override
    public ScheduleResponseDto assignRoomToClass(ScheduleRequestDto dto) {
        // 1. Check if the class is already assigned to a room
        if (scheduleRepository.existsByaClass_ClassId(dto.getClassId())) {
            throw new IllegalStateException("This class (ID: " + dto.getClassId() + ") is already assigned to a room.");
        }

        // 2. Fetch the related entities
        Class aClass = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + dto.getClassId()));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new NoSuchElementException("Room not found with id: " + dto.getRoomId()));

        // 3. Create and save the new schedule entry
        Schedule newSchedule = new Schedule();
        newSchedule.setAClass(aClass);
        newSchedule.setRoom(room);

        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        // 4. Convert to DTO to return to the controller
        return new ScheduleResponseDto(
                savedSchedule.getScheduleId(),
                savedSchedule.getAClass().getClassId(),
                savedSchedule.getAClass().getClassName(),
                savedSchedule.getRoom().getRoomId(),
                savedSchedule.getRoom().getRoomName(),
                savedSchedule.getRoom().getBuildingName()
        );
    }
}