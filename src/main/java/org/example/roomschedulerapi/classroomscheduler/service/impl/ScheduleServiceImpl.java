package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.*;
import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.model.dto.*;
import org.example.roomschedulerapi.classroomscheduler.exception.ResourceNotFoundException;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;
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
        Set<Long> unarchivedClassIds = classRepository.findAll().stream()
                .filter(c -> !c.isArchived())
                .map(Class::getClassId)
                .collect(Collectors.toSet());

        List<Schedule> permanentSchedules = scheduleRepository.findSchedulesByInstructorId(instructorId)
                .stream()
                .filter(schedule -> unarchivedClassIds.contains(schedule.getAClass().getClassId()))
                .collect(Collectors.toList());

        List<ChangeRequest> allOverrides = changeRequestRepository.findActiveApprovedChangesForSchedules(permanentSchedules, LocalDate.now());

        Set<Long> movedScheduleIds = allOverrides.stream()
                .filter(cr -> cr.getOriginalSchedule() != null)
                .map(cr -> cr.getOriginalSchedule().getScheduleId())
                .collect(Collectors.toSet());

        List<ScheduleResponseDto> finalSchedules = new ArrayList<>();

        for (Schedule schedule : permanentSchedules) {
            if (!movedScheduleIds.contains(schedule.getScheduleId())) {
                finalSchedules.add(convertToDtoForInstructor(schedule, instructorId));
            }
        }

        for (ChangeRequest override : allOverrides) {
            if (override.getOriginalSchedule() != null) {
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
                .map(ci -> new DayDetailDto(ci.getDayOfWeek().name(), ci.isOnline(), ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName(), null, null, null, null, null, null))
                .collect(Collectors.toList());

        return new ScheduleResponseDto(
                schedule.getScheduleId(), aClass.getClassId(), aClass.getClassName(), dayDetails,
                aClass.getGeneration(), aClass.getSemester(), shiftDto, room.getRoomId(),
                room.getRoomName(), room.getBuildingName(), aClass.getMajorName(), aClass.isArchived()
        );
    }

    private ScheduleResponseDto convertToDtoForInstructor(Schedule schedule, Long instructorId) {
        Class aClass = schedule.getAClass();
        Room room = schedule.getRoom();
        Shift shiftEntity = aClass.getShift();
        ShiftResponseDto shiftDto = new ShiftResponseDto(shiftEntity.getShiftId(), shiftEntity.getStartTime(), shiftEntity.getEndTime(), shiftEntity.getName());

        List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                .filter(ci -> ci.getInstructor().getInstructorId().equals(instructorId))
                .map(ci -> new DayDetailDto(ci.getDayOfWeek().name(), ci.isOnline(), ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName(), null, null, null, null, null, null))
                .collect(Collectors.toList());

        return new ScheduleResponseDto(
                schedule.getScheduleId(), aClass.getClassId(), aClass.getClassName(), dayDetails,
                aClass.getGeneration(), aClass.getSemester(), shiftDto, room.getRoomId(),
                room.getRoomName(), room.getBuildingName(), aClass.getMajorName(), aClass.isArchived()
        );
    }


    private ScheduleResponseDto convertToDtoFromOverride(Schedule originalSchedule, ChangeRequest override) {
        ScheduleResponseDto dto = convertToDto(originalSchedule);
        Room tempRoom = override.getTemporaryRoom();
        LocalDate effectiveDate = override.getEffectiveDate();

        dto.getDayDetails().forEach(detail -> {
            if (effectiveDate.getDayOfWeek().name().equalsIgnoreCase(detail.getDayOfWeek())) {
                detail.setEffectiveDate(effectiveDate);
                detail.setEventName(override.getDescription());
                detail.setTemporaryRoomId(tempRoom.getRoomId());
                detail.setTemporaryRoomName(tempRoom.getRoomName());
                detail.setTemporaryBuildingName(tempRoom.getBuildingName());
                if (override.getShift() != null) {
                    Shift tempShift = override.getShift();
                    detail.setTemporaryShift(new ShiftResponseDto(tempShift.getShiftId(), tempShift.getStartTime(), tempShift.getEndTime(), tempShift.getName()));
                }
            }
        });
        return dto;
    }


    private ScheduleResponseDto convertToDtoFromOverride(Schedule originalSchedule, Long instructorId, ChangeRequest override) {
        ScheduleResponseDto dto = convertToDtoForInstructor(originalSchedule, instructorId);
        Room tempRoom = override.getTemporaryRoom();
        LocalDate effectiveDate = override.getEffectiveDate();

        dto.getDayDetails().forEach(detail -> {
            if (effectiveDate.getDayOfWeek().name().equalsIgnoreCase(detail.getDayOfWeek())) {
                detail.setEffectiveDate(effectiveDate);
                detail.setEventName(override.getDescription());
                detail.setTemporaryRoomId(tempRoom.getRoomId());
                detail.setTemporaryRoomName(tempRoom.getRoomName());
                detail.setTemporaryBuildingName(tempRoom.getBuildingName());
                if (override.getShift() != null) {
                    Shift tempShift = override.getShift();
                    detail.setTemporaryShift(new ShiftResponseDto(tempShift.getShiftId(), tempShift.getStartTime(), tempShift.getEndTime(), tempShift.getName()));
                }
            }
        });
        return dto;
    }


    private ScheduleResponseDto convertToDtoFromConferenceRoomBooking(ChangeRequest changeRequest) {
        Room room = changeRequest.getTemporaryRoom();
        Instructor instructor = changeRequest.getRequestingInstructor();
        ShiftResponseDto tempShiftDto = null;
        if (changeRequest.getShift() != null) {
            Shift tempShift = changeRequest.getShift();
            tempShiftDto = new ShiftResponseDto(tempShift.getShiftId(), tempShift.getStartTime(), tempShift.getEndTime(), tempShift.getName());
        }

        List<DayDetailDto> dayDetails = new ArrayList<>();
        dayDetails.add(new DayDetailDto(
                changeRequest.getEffectiveDate().getDayOfWeek().name(),
                false,
                instructor.getFirstName() + " " + instructor.getLastName(),
                changeRequest.getDescription(),
                room.getRoomId(),
                room.getRoomName(),
                room.getBuildingName(),
                changeRequest.getEffectiveDate(),
                tempShiftDto
        ));

        String className = (changeRequest.getDescription() != null && !changeRequest.getDescription().isEmpty())
                ? changeRequest.getDescription()
                : "Booked by " + instructor.getFirstName() + " " + instructor.getLastName();

        return new ScheduleResponseDto(
                null, null, className, dayDetails, null, null,
                null, room.getRoomId(), room.getRoomName(), room.getBuildingName(),
                "Conference Room", false
        );
    }

    private ScheduleResponseDto convertUnscheduledClassToDto(Class aClass) {
        Shift shiftEntity = aClass.getShift();
        ShiftResponseDto shiftDto = new ShiftResponseDto(
                shiftEntity.getShiftId(),
                shiftEntity.getStartTime(),
                shiftEntity.getEndTime(),
                shiftEntity.getName()
        );

        boolean isClassOnline = aClass.getClassInstructors().stream().anyMatch(ClassInstructor::isOnline);
        String roomName = isClassOnline ? "Online" : "Unassigned";
        String buildingName = isClassOnline ? "N/A" : "TBD";

        List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                .map(ci -> new DayDetailDto(
                        ci.getDayOfWeek().name(),
                        ci.isOnline(),
                        ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName(),
                        null, null, null, null, null, null
                ))
                .collect(Collectors.toList());

        return new ScheduleResponseDto(
                null, aClass.getClassId(), aClass.getClassName(), dayDetails,
                aClass.getGeneration(), aClass.getSemester(), shiftDto, null,
                roomName, buildingName, aClass.getMajorName(), aClass.isArchived()
        );
    }

    @Override
    @Transactional
    public ScheduleResponseDto revertTemporaryMove(Long scheduleId) {
        Schedule originalSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        List<ChangeRequest> changeRequests = changeRequestRepository.findAllByOriginalSchedule_ScheduleId(scheduleId);

        if (changeRequests.isEmpty()) {
            System.out.println("No active change request found to revert for schedule ID: " + scheduleId);
        } else {
            for (ChangeRequest request : changeRequests) {
                if (request.getStatus() == RequestStatus.PENDING || request.getStatus() == RequestStatus.APPROVED) {
                    request.setStatus(RequestStatus.DENIED);
                }
            }
            changeRequestRepository.saveAll(changeRequests);
        }
        return convertToDto(originalSchedule);
    }
}