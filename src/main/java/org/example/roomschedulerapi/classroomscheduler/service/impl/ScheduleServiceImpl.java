package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.*;
import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.model.dto.*;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;
import org.example.roomschedulerapi.classroomscheduler.exception.ResourceNotFoundException;
import org.example.roomschedulerapi.classroomscheduler.repository.ChangeRequestRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.ClassRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.RoomRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.ScheduleRepository;
import org.example.roomschedulerapi.classroomscheduler.service.ScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;
    private final RoomRepository roomRepository;
    private final ChangeRequestRepository changeRequestRepository;

    @Override
    public List<ScheduleResponseDto> getSchedulesForInstructor(Long instructorId) {
        // 1. Get all permanent schedules for the instructor.
        List<Schedule> permanentSchedules = scheduleRepository.findSchedulesByInstructorId(instructorId);

        // 2. Find all approved change requests linked to these permanent schedules.
        List<ChangeRequest> allOverrides = changeRequestRepository.findActiveApprovedChangesForSchedules(permanentSchedules, LocalDate.now());

        // 3. Create a Set of schedule IDs that have been moved.
        Set<Long> movedScheduleIds = allOverrides.stream()
                .map(cr -> cr.getOriginalSchedule().getScheduleId())
                .collect(Collectors.toSet());

        List<ScheduleResponseDto> finalSchedules = new ArrayList<>();

        // 4. Add permanent schedules that have NOT been moved.
        for (Schedule schedule : permanentSchedules) {
            if (!movedScheduleIds.contains(schedule.getScheduleId())) {
                finalSchedules.add(convertToDtoForInstructor(schedule, instructorId));
            }
        }

        // 5. Add the temporary schedules from the overrides.
        for (ChangeRequest override : allOverrides) {
            finalSchedules.add(convertToDtoFromOverride(override.getOriginalSchedule(), instructorId, override));
        }

        return finalSchedules;
    }

    @Override
    @Transactional
    public void swapSchedules(ScheduleSwapDto swapDto) {
        if (swapDto.getScheduleId1().equals(swapDto.getScheduleId2())) {
            // No need to swap if it's the same schedule
            return;
        }

        // Find both schedules, or throw an exception if not found
        Schedule schedule1 = scheduleRepository.findById(swapDto.getScheduleId1())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", swapDto.getScheduleId1()));

        Schedule schedule2 = scheduleRepository.findById(swapDto.getScheduleId2())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", swapDto.getScheduleId2()));

        // Get the rooms from each schedule
        Room room1 = schedule1.getRoom();
        Room room2 = schedule2.getRoom();

        // Swap the rooms
        schedule1.setRoom(room2);
        schedule2.setRoom(room1);

        // Save both updated schedules to the database
        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);
    }


    @Override
    public List<ScheduleResponseDto> getAllClassesWithScheduleStatus() {
        // This method now uses the same robust logic.
        List<Schedule> allPermanentSchedules = scheduleRepository.findAll();
        List<ChangeRequest> allOverrides = changeRequestRepository.findAllActiveApprovedChanges(LocalDate.now());

        Set<Long> movedScheduleIds = allOverrides.stream()
                .map(cr -> cr.getOriginalSchedule().getScheduleId())
                .collect(Collectors.toSet());

        List<ScheduleResponseDto> finalSchedules = new ArrayList<>();

        // Add permanent schedules that have not been moved.
        for (Schedule schedule : allPermanentSchedules) {
            if (!movedScheduleIds.contains(schedule.getScheduleId())) {
                finalSchedules.add(convertToDto(schedule));
            }
        }

        // Add the temporary schedules from the overrides.
        for (ChangeRequest override : allOverrides) {
            finalSchedules.add(convertToDtoFromOverride(override.getOriginalSchedule(), override));
        }

        return finalSchedules;
    }


    // --- UNCHANGED PUBLIC METHODS ---

    @Override
    public List<ScheduleResponseDto> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScheduleResponseDto moveSchedule(Long scheduleId, Long newRoomId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));
        Room newRoom = roomRepository.findById(newRoomId).orElseThrow(() -> new ResourceNotFoundException("Room", "id", newRoomId));
        schedule.setRoom(newRoom);
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return convertToDto(updatedSchedule); // Use manual mapping
    }

    @Override
    public Optional<ScheduleResponseDto> getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).map(this::convertToDto);
    }

    @Transactional
    @Override
    public ScheduleResponseDto assignRoomToClass(ScheduleRequestDto dto) {
        Class aClass = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + dto.getClassId()));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new NoSuchElementException("Room not found with id: " + dto.getRoomId()));
        if (scheduleRepository.existsByaClass(aClass)) {
            throw new IllegalStateException("Class " + aClass.getClassName() + " is already assigned to a room");
        }
        List<DaysOfWeek> classDays = aClass.getClassInstructors().stream()
                .map(ClassInstructor::getDayOfWeek).collect(Collectors.toList());
        if (classDays.isEmpty()) {
            throw new IllegalStateException("Class has no scheduled days");
        }
        Shift classShift = aClass.getShift();
        if (classShift == null) {
            throw new IllegalStateException("Class has no assigned shift");
        }
        List<Schedule> conflictingSchedules = scheduleRepository.findConflictingSchedules(
                room.getRoomId(), classDays, classShift.getStartTime(), classShift.getEndTime()
        );
        if (!conflictingSchedules.isEmpty()) {
            String conflictingClasses = conflictingSchedules.stream()
                    .map(s -> s.getAClass().getClassName()).collect(Collectors.joining(", "));
            throw new IllegalStateException(
                    "Room " + room.getRoomName() + " is already occupied by class(es): " +
                            conflictingClasses + " during the requested time"
            );
        }
        Schedule newSchedule = new Schedule();
        newSchedule.setAClass(aClass);
        newSchedule.setRoom(room);
        Schedule savedSchedule = scheduleRepository.save(newSchedule);
        return convertToDto(savedSchedule);
    }

    @Transactional
    @Override
    public void unassignRoomFromClass(Long scheduleId) {
        // First, check if the schedule exists to provide a clear error message.
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new NoSuchElementException("Schedule not found with id: " + scheduleId);
        }

        // --- FIX: Delete all associated change requests FIRST ---
        changeRequestRepository.deleteAllByOriginalSchedule_ScheduleId(scheduleId);

        // Now it is safe to delete the schedule itself.
        scheduleRepository.deleteById(scheduleId);
    }

    // --- PRIVATE HELPER METHODS ---

    private ScheduleResponseDto convertToDto(Schedule schedule) {
        Class aClass = schedule.getAClass();
        Room room = schedule.getRoom();
        Shift shiftEntity = aClass.getShift();
        ShiftResponseDto shiftDto = new ShiftResponseDto(shiftEntity.getShiftId(), shiftEntity.getStartTime(), shiftEntity.getEndTime());
        List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                .map(ci -> new DayDetailDto(ci.getDayOfWeek().name(), ci.isOnline(), ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName()))
                .collect(Collectors.toList());

        return new ScheduleResponseDto(
                schedule.getScheduleId(), aClass.getClassId(), aClass.getClassName(), dayDetails,
                aClass.getGeneration(), aClass.getSemester(), shiftDto, room.getRoomId(),
                room.getRoomName(), room.getBuildingName(), aClass.getMajorName()
        );
    }

    private ScheduleResponseDto convertToDtoForInstructor(Schedule schedule, Long instructorId) {
        Class aClass = schedule.getAClass();
        Room room = schedule.getRoom();
        Shift shiftEntity = aClass.getShift();
        ShiftResponseDto shiftDto = new ShiftResponseDto(shiftEntity.getShiftId(), shiftEntity.getStartTime(), shiftEntity.getEndTime());

        List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                .filter(ci -> ci.getInstructor().getInstructorId().equals(instructorId))
                .map(ci -> new DayDetailDto(ci.getDayOfWeek().name(), ci.isOnline(), ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName()))
                .collect(Collectors.toList());

        return new ScheduleResponseDto(
                schedule.getScheduleId(), aClass.getClassId(), aClass.getClassName(), dayDetails,
                aClass.getGeneration(), aClass.getSemester(), shiftDto, room.getRoomId(),
                room.getRoomName(), room.getBuildingName(), aClass.getMajorName()
        );
    }

    private ScheduleResponseDto convertToDtoFromOverride(Schedule originalSchedule, ChangeRequest override) {
        ScheduleResponseDto dto = convertToDto(originalSchedule);
        dto.setRoomId(override.getTemporaryRoom().getRoomId());
        dto.setRoomName(override.getTemporaryRoom().getRoomName() + " (Temp)");
        dto.setBuildingName(override.getTemporaryRoom().getBuildingName());
        String newDayOfWeek = override.getEffectiveDate().getDayOfWeek().name();
        dto.getDayDetails().forEach(dd -> dd.setDayOfWeek(newDayOfWeek));
        return dto;
    }

    private ScheduleResponseDto convertToDtoFromOverride(Schedule originalSchedule, Long instructorId, ChangeRequest override) {
        ScheduleResponseDto dto = convertToDtoForInstructor(originalSchedule, instructorId);
        dto.setRoomId(override.getTemporaryRoom().getRoomId());
        dto.setRoomName(override.getTemporaryRoom().getRoomName() + " (Temp)");
        dto.setBuildingName(override.getTemporaryRoom().getBuildingName());
        String newDayOfWeek = override.getEffectiveDate().getDayOfWeek().name();
        dto.getDayDetails().forEach(dd -> dd.setDayOfWeek(newDayOfWeek));
        return dto;
    }

    private ScheduleResponseDto convertUnscheduledClassToDto(Class aClass) {
        Shift shiftEntity = aClass.getShift();
        ShiftResponseDto shiftDto = new ShiftResponseDto(shiftEntity.getShiftId(), shiftEntity.getStartTime(), shiftEntity.getEndTime());
        boolean isClassOnline = aClass.getClassInstructors().stream().anyMatch(ClassInstructor::isOnline);
        String roomName = isClassOnline ? "Online" : "Unassigned";
        String buildingName = isClassOnline ? "N/A" : "TBD";
        List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                .map(ci -> new DayDetailDto(ci.getDayOfWeek().name(), ci.isOnline(), ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName()))
                .collect(Collectors.toList());
        return new ScheduleResponseDto(
                null, aClass.getClassId(), aClass.getClassName(), dayDetails, aClass.getGeneration(),
                aClass.getSemester(), shiftDto, null, roomName, buildingName, aClass.getMajorName()
        );
    }
}
