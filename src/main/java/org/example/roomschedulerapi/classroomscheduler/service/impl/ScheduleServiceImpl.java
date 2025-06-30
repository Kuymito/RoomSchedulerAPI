package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.model.Room;
import org.example.roomschedulerapi.classroomscheduler.model.Schedule;
import org.example.roomschedulerapi.classroomscheduler.model.Shift;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ShiftResponseDto;
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
        Class aClass = schedule.getAClass();
        Room room = schedule.getRoom();

        // --- FIX: Manually map the Shift entity to a ShiftResponseDto ---
        Shift shiftEntity = aClass.getShiftEntity();
        ShiftResponseDto shiftDto = new ShiftResponseDto(
                shiftEntity.getShiftId(), // Assuming shiftId is Long in entity
                shiftEntity.getStartTime(),
                shiftEntity.getEndTime()
        );

        return new ScheduleResponseDto(
                schedule.getScheduleId(),
                aClass.getClassId(),
                aClass.getClassName(),
                aClass.getDay(),
                aClass.getYear(),
                aClass.getSemester(),
                shiftDto, // Use the newly created DTO
                room.getRoomId(),
                room.getRoomName(),
                room.getBuildingName()
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
        // ... (existing logic to fetch entities) ...
        Class aClass = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + dto.getClassId()));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new NoSuchElementException("Room not found with id: " + dto.getRoomId()));

        Schedule newSchedule = new Schedule();
        newSchedule.setAClass(aClass);
        newSchedule.setRoom(room);
        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        // This will now correctly call the updated convertToDto method
        return convertToDto(savedSchedule);
    }


    @Override
    public List<ScheduleResponseDto> getSchedulesForInstructor(Long instructorId) {
        // Call the new repository method
        List<Schedule> schedules = scheduleRepository.findByaClass_Instructor_InstructorId(instructorId);

        // Reuse the existing DTO conversion logic
        return schedules.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}