package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.*;
import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.model.dto.*;
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
        // 1. Fetch all classes and create a set of un-archived class IDs.
        Set<Long> unarchivedClassIds = classRepository.findAll().stream()
                .filter(c -> !c.isArchived())
                .map(Class::getClassId)
                .collect(Collectors.toSet());

        // 2. Get all permanent schedules for the instructor, filtering by un-archived classes.
        List<Schedule> permanentSchedules = scheduleRepository.findSchedulesByInstructorId(instructorId)
                .stream()
                .filter(schedule -> unarchivedClassIds.contains(schedule.getAClass().getClassId()))
                .collect(Collectors.toList());

        // 3. Find all approved change requests linked to these permanent schedules.
        List<ChangeRequest> allOverrides = changeRequestRepository.findActiveApprovedChangesForSchedules(permanentSchedules, LocalDate.now());

        // 4. Create a Set of schedule IDs that have been moved, ignoring any with null original schedules.
        Set<Long> movedScheduleIds = allOverrides.stream()
                .filter(cr -> cr.getOriginalSchedule() != null) // FIX: Prevent NullPointerException
                .map(cr -> cr.getOriginalSchedule().getScheduleId())
                .collect(Collectors.toSet());

        List<ScheduleResponseDto> finalSchedules = new ArrayList<>();

        // 5. Add permanent schedules that have NOT been moved.
        for (Schedule schedule : permanentSchedules) {
            if (!movedScheduleIds.contains(schedule.getScheduleId())) {
                finalSchedules.add(convertToDtoForInstructor(schedule, instructorId));
            }
        }

        // 6. Add the temporary schedules from the overrides.
        for (ChangeRequest override : allOverrides) {
            if (override.getOriginalSchedule() != null) { // FIX: Prevent NullPointerException
                finalSchedules.add(convertToDtoFromOverride(override.getOriginalSchedule(), instructorId, override));
            }
        }

        return finalSchedules;
    }

    @Override
    @Transactional
    public void swapSchedules(ScheduleSwapDto swapDto) {
        if (swapDto.getScheduleId1().equals(swapDto.getScheduleId2())) {
            return;
        }

        Schedule schedule1 = scheduleRepository.findById(swapDto.getScheduleId1())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", swapDto.getScheduleId1()));
        Schedule schedule2 = scheduleRepository.findById(swapDto.getScheduleId2())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", swapDto.getScheduleId2()));

        Room room1 = schedule1.getRoom();
        Room room2 = schedule2.getRoom();

        schedule1.setRoom(room2);
        schedule2.setRoom(room1);

        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);
    }


    @Override
    public List<ScheduleResponseDto> getAllClassesWithScheduleStatus() {
        Set<Long> unarchivedClassIds = classRepository.findAll().stream()
                .filter(c -> !c.isArchived())
                .map(Class::getClassId)
                .collect(Collectors.toSet());

        List<Schedule> allPermanentSchedules = scheduleRepository.findAll().stream()
                .filter(schedule -> unarchivedClassIds.contains(schedule.getAClass().getClassId()))
                .collect(Collectors.toList());

        List<ChangeRequest> allOverrides = changeRequestRepository.findAllActiveApprovedChanges(LocalDate.now());

        Set<Long> movedScheduleIds = allOverrides.stream()
                .filter(cr -> cr.getOriginalSchedule() != null)
                .map(cr -> cr.getOriginalSchedule().getScheduleId())
                .collect(Collectors.toSet());

        List<ScheduleResponseDto> finalSchedules = new ArrayList<>();

        for (Schedule schedule : allPermanentSchedules) {
            if (!movedScheduleIds.contains(schedule.getScheduleId())) {
                finalSchedules.add(convertToDto(schedule));
            }
        }

        for (ChangeRequest override : allOverrides) {
            if (override.getOriginalSchedule() != null) {
                if (unarchivedClassIds.contains(override.getOriginalSchedule().getAClass().getClassId())) {
                    finalSchedules.add(convertToDtoFromOverride(override.getOriginalSchedule(), override));
                }
            } else {
                finalSchedules.add(convertToDtoFromConferenceRoomBooking(override));
            }
        }

        return finalSchedules;
    }


    @Override
    public List<ScheduleResponseDto> getAllSchedules() {
        Set<Long> unarchivedClassIds = classRepository.findAll().stream()
                .filter(c -> !c.isArchived())
                .map(Class::getClassId)
                .collect(Collectors.toSet());

        return scheduleRepository.findAll().stream()
                .filter(schedule -> unarchivedClassIds.contains(schedule.getAClass().getClassId()))
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
        return convertToDto(updatedSchedule);
    }

    @Override
    public Optional<ScheduleResponseDto> getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .filter(schedule -> !schedule.getAClass().isArchived())
                .map(this::convertToDto);
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
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new NoSuchElementException("Schedule not found with id: " + scheduleId);
        }
        changeRequestRepository.deleteAllByOriginalSchedule_ScheduleId(scheduleId);
        scheduleRepository.deleteById(scheduleId);
    }

    private ScheduleResponseDto convertToDto(Schedule schedule) {
        Class aClass = schedule.getAClass();
        Room room = schedule.getRoom();
        Shift shiftEntity = aClass.getShift();
        ShiftResponseDto shiftDto = new ShiftResponseDto(shiftEntity.getShiftId(), shiftEntity.getStartTime(), shiftEntity.getEndTime(), shiftEntity.getName());
        List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                .map(ci -> new DayDetailDto(ci.getDayOfWeek().name(), ci.isOnline(), ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName()))
                .collect(Collectors.toList());

        return new ScheduleResponseDto(
                schedule.getScheduleId(), aClass.getClassId(), aClass.getClassName(), dayDetails,
                aClass.getGeneration(), aClass.getSemester(), shiftDto, room.getRoomId(),
                room.getRoomName(), room.getBuildingName(), aClass.getMajorName(), aClass.isArchived(),
                null, null, null, null // EventName and temporary fields are null for a regular schedule
        );
    }

    private ScheduleResponseDto convertToDtoForInstructor(Schedule schedule, Long instructorId) {
        Class aClass = schedule.getAClass();
        Room room = schedule.getRoom();
        Shift shiftEntity = aClass.getShift();
        ShiftResponseDto shiftDto = new ShiftResponseDto(shiftEntity.getShiftId(), shiftEntity.getStartTime(), shiftEntity.getEndTime(), shiftEntity.getName());

        List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                .filter(ci -> ci.getInstructor().getInstructorId().equals(instructorId))
                .map(ci -> new DayDetailDto(ci.getDayOfWeek().name(), ci.isOnline(), ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName()))
                .collect(Collectors.toList());

        return new ScheduleResponseDto(
                schedule.getScheduleId(), aClass.getClassId(), aClass.getClassName(), dayDetails,
                aClass.getGeneration(), aClass.getSemester(), shiftDto, room.getRoomId(),
                room.getRoomName(), room.getBuildingName(), aClass.getMajorName(), aClass.isArchived(),
                null, null, null, null // EventName and temporary fields are null for a regular schedule
        );
    }

    private ScheduleResponseDto convertToDtoFromOverride(Schedule originalSchedule, ChangeRequest override) {
        // Start with the original schedule's data
        ScheduleResponseDto dto = convertToDto(originalSchedule);

        // Set the temporary room details from the override
        dto.setTemporaryRoomId(override.getTemporaryRoom().getRoomId());
        dto.setTemporaryRoomName(override.getTemporaryRoom().getRoomName());
        dto.setTemporaryBuildingName(override.getTemporaryRoom().getBuildingName());

        // Update the day of the week based on the override's effective date
        String newDayOfWeek = override.getEffectiveDate().getDayOfWeek().name();
        dto.getDayDetails().forEach(dd -> dd.setDayOfWeek(newDayOfWeek));

        // Set the event name from the change request
        dto.setEventName(override.getEventName());

        return dto;
    }
    private ScheduleResponseDto convertToDtoFromOverride(Schedule originalSchedule, Long instructorId, ChangeRequest override) {
        // Start with the original schedule's data for the specific instructor
        ScheduleResponseDto dto = convertToDtoForInstructor(originalSchedule, instructorId);

        // Set the temporary room details from the override
        dto.setTemporaryRoomId(override.getTemporaryRoom().getRoomId());
        dto.setTemporaryRoomName(override.getTemporaryRoom().getRoomName());
        dto.setTemporaryBuildingName(override.getTemporaryRoom().getBuildingName());

        // Update the day of the week based on the override's effective date
        String newDayOfWeek = override.getEffectiveDate().getDayOfWeek().name();
        dto.getDayDetails().forEach(dd -> dd.setDayOfWeek(newDayOfWeek));

        // Set the event name from the change request
        dto.setEventName(override.getEventName());

        return dto;
    }

    private ScheduleResponseDto convertToDtoFromConferenceRoomBooking(ChangeRequest changeRequest) {
        Room room = changeRequest.getTemporaryRoom();
        Instructor instructor = changeRequest.getRequestingInstructor();

        List<DayDetailDto> dayDetails = new ArrayList<>();
        dayDetails.add(new DayDetailDto(changeRequest.getEffectiveDate().getDayOfWeek().name(), false, instructor.getFirstName() + " " + instructor.getLastName()));

        ShiftResponseDto shiftDto = new ShiftResponseDto(null, null, null, "Booked");

        String className = (changeRequest.getEventName() != null && !changeRequest.getEventName().isEmpty())
                ? changeRequest.getEventName()
                : "Booked by " + instructor.getFirstName() + " " + instructor.getLastName();

        return new ScheduleResponseDto(
                null, null, className, dayDetails, null, null,
                shiftDto, room.getRoomId(), room.getRoomName(), room.getBuildingName(),
                "Conference Room", false, changeRequest.getEventName(),
                null, null, null // No temporary room for a direct booking
        );
    }

    private ScheduleResponseDto convertUnscheduledClassToDto(Class aClass) {
        Shift shiftEntity = aClass.getShift();
        ShiftResponseDto shiftDto = new ShiftResponseDto(shiftEntity.getShiftId(), shiftEntity.getStartTime(), shiftEntity.getEndTime(), shiftEntity.getName());
        boolean isClassOnline = aClass.getClassInstructors().stream().anyMatch(ClassInstructor::isOnline);
        String roomName = isClassOnline ? "Online" : "Unassigned";
        String buildingName = isClassOnline ? "N/A" : "TBD";
        List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                .map(ci -> new DayDetailDto(ci.getDayOfWeek().name(), ci.isOnline(), ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName()))
                .collect(Collectors.toList());

        return new ScheduleResponseDto(
                null, aClass.getClassId(), aClass.getClassName(), dayDetails, aClass.getGeneration(),
                aClass.getSemester(), shiftDto, null, roomName, buildingName, aClass.getMajorName(), aClass.isArchived(),
                null, null, null, null // EventName and temporary fields are null for an unscheduled class
        );
    }
}