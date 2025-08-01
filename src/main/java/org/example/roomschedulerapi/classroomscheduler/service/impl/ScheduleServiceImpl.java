package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.exception.ResourceNotFoundException;
import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.model.ChangeRequest;
import org.example.roomschedulerapi.classroomscheduler.model.DaysOfWeek;
import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.example.roomschedulerapi.classroomscheduler.model.Room;
import org.example.roomschedulerapi.classroomscheduler.model.Schedule;
import org.example.roomschedulerapi.classroomscheduler.model.Shift;
import org.example.roomschedulerapi.classroomscheduler.model.dto.DayDetailDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleSwapDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ShiftResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;
import org.example.roomschedulerapi.classroomscheduler.repository.ChangeRequestRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.ClassRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.RoomRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.ScheduleRepository;
import org.example.roomschedulerapi.classroomscheduler.service.ScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
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
        // 1. Fetch the Class and the Room.
        Class aClass = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + dto.getClassId()));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new NoSuchElementException("Room not found with id: " + dto.getRoomId()));

        // **REVERTED LOGIC**:
        // A schedule for this class should not exist yet. Check for duplicates just in case.
        if (scheduleRepository.existsByaClass(aClass)) {
            throw new IllegalStateException("This class is already scheduled in a room. To move it, please drag and drop the schedule.");
        }

        // 2. Perform Conflict Checking.
        List<DaysOfWeek> classDays = aClass.getClassInstructors().stream()
                .map(ci -> ci.getDayOfWeek()).collect(Collectors.toList());

        if (classDays.isEmpty()) {
            throw new IllegalStateException("Cannot schedule class: No instructors or days have been assigned.");
        }

        Shift classShift = aClass.getShift();
        if (classShift == null) {
            throw new IllegalStateException("Class has no assigned shift.");
        }

        List<Schedule> conflictingSchedules = scheduleRepository.findConflictingSchedules(
                room.getRoomId(), classDays, classShift.getStartTime(), classShift.getEndTime()
        );

        if (!conflictingSchedules.isEmpty()) {
            String conflictingClasses = conflictingSchedules.stream()
                    .map(s -> s.getAClass().getClassName()).collect(Collectors.joining(", "));
            throw new IllegalStateException(
                    "Room " + room.getRoomName() + " is already occupied by class(es): " +
                            conflictingClasses + " during the requested time."
            );
        }

        // 3. Create and save the NEW Schedule.
        Schedule newSchedule = new Schedule();
        newSchedule.setAClass(aClass);
        newSchedule.setRoom(room);

        Schedule savedSchedule = scheduleRepository.save(newSchedule);
        return convertToDto(savedSchedule);
    }

    @Override
    @Transactional
    public void unassignRoomFromClass(Long scheduleId) {
        // 1. Find the schedule to ensure it exists.
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found with id: " + scheduleId));

        // 2. Find all change requests that reference this schedule.
        List<ChangeRequest> associatedRequests = changeRequestRepository.findAllByOriginalSchedule_ScheduleId(scheduleId);

        // 3. Explicitly delete the associated change requests.
        // This ensures all child records are removed before the parent.
        if (associatedRequests != null && !associatedRequests.isEmpty()) {
            changeRequestRepository.deleteAll(associatedRequests);
        }

        // 4. Now that all references are gone, it is safe to delete the schedule.
        scheduleRepository.delete(schedule);
    }

    private ScheduleResponseDto convertToDto(Schedule schedule) {
        Class aClass = schedule.getAClass();
        Room room = schedule.getRoom();
        Shift shiftEntity = aClass.getShift();
        ShiftResponseDto shiftDto = new ShiftResponseDto(shiftEntity.getShiftId(), shiftEntity.getStartTime(), shiftEntity.getEndTime(), shiftEntity.getName());

        List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                .map(ci -> new DayDetailDto(
                        ci.getDayOfWeek().name(),
                        ci.isOnline(),
                        ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName(),
                        null, null, null, null, null, null, null // All temporary fields are null
                ))
                .collect(Collectors.toList());

        String roomName = (room != null) ? room.getRoomName() : "Unassigned";
        String buildingName = (room != null) ? room.getBuildingName() : "TBD";
        Long roomId = (room != null) ? room.getRoomId() : null;

        return new ScheduleResponseDto(
                schedule.getScheduleId(), aClass.getClassId(), aClass.getClassName(), dayDetails,
                aClass.getGeneration(), aClass.getSemester(), shiftDto, roomId,
                roomName, buildingName, aClass.getMajorName(), aClass.isArchived()
        );
    }

    private ScheduleResponseDto convertToDtoForInstructor(Schedule schedule, Long instructorId) {
        Class aClass = schedule.getAClass();
        Room room = schedule.getRoom();
        Shift shiftEntity = aClass.getShift();
        ShiftResponseDto shiftDto = new ShiftResponseDto(shiftEntity.getShiftId(), shiftEntity.getStartTime(), shiftEntity.getEndTime(), shiftEntity.getName());

        List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                .filter(ci -> ci.getInstructor().getInstructorId().equals(instructorId))
                .map(ci -> new DayDetailDto(
                        ci.getDayOfWeek().name(),
                        ci.isOnline(),
                        ci.getInstructor().getFirstName() + " " + ci.getInstructor().getLastName(),
                        null, null, null, null, null, null, null // All temporary fields are null
                ))
                .collect(Collectors.toList());

        String roomName = (room != null) ? room.getRoomName() : "Unassigned";
        String buildingName = (room != null) ? room.getBuildingName() : "TBD";
        Long roomId = (room != null) ? room.getRoomId() : null;

        return new ScheduleResponseDto(
                schedule.getScheduleId(), aClass.getClassId(), aClass.getClassName(), dayDetails,
                aClass.getGeneration(), aClass.getSemester(), shiftDto, roomId,
                roomName, buildingName, aClass.getMajorName(), aClass.isArchived()
        );
    }


    private ScheduleResponseDto convertToDtoFromOverride(Schedule originalSchedule, ChangeRequest override) {
        ScheduleResponseDto dto = convertToDto(originalSchedule);
        if (!dto.getDayDetails().isEmpty()) {
            DayDetailDto dayDetailToUpdate = dto.getDayDetails().get(0);
            applyTemporaryDetails(dayDetailToUpdate, override);
        }
        return dto;
    }


    private ScheduleResponseDto convertToDtoFromOverride(Schedule originalSchedule, Long instructorId, ChangeRequest override) {
        ScheduleResponseDto dto = convertToDtoForInstructor(originalSchedule, instructorId);
        if (!dto.getDayDetails().isEmpty()) {
            DayDetailDto dayDetailToUpdate = dto.getDayDetails().get(0);
            applyTemporaryDetails(dayDetailToUpdate, override);
        }
        return dto;
    }

    private void applyTemporaryDetails(DayDetailDto dayDetail, ChangeRequest override) {
        Room tempRoom = override.getTemporaryRoom();
        LocalDate effectiveDate = override.getEffectiveDate();

        dayDetail.setEventName(override.getDescription());
        dayDetail.setEffectiveDate(effectiveDate);
        dayDetail.setTemporaryDay(effectiveDate.getDayOfWeek().name());
        dayDetail.setTemporaryRoomId(tempRoom.getRoomId());
        dayDetail.setTemporaryRoomName(tempRoom.getRoomName());
        dayDetail.setTemporaryBuildingName(tempRoom.getBuildingName());

        if (override.getShift() != null) {
            Shift tempShift = override.getShift();
            dayDetail.setTemporaryShift(new ShiftResponseDto(
                    tempShift.getShiftId(),
                    tempShift.getStartTime(),
                    tempShift.getEndTime(),
                    tempShift.getName()
            ));
        }
    }


    private ScheduleResponseDto convertToDtoFromConferenceRoomBooking(ChangeRequest changeRequest) {
        Room room = changeRequest.getTemporaryRoom();
        Instructor instructor = changeRequest.getRequestingInstructor();
        ShiftResponseDto tempShiftDto = null;
        if (changeRequest.getShift() != null) {
            Shift tempShift = changeRequest.getShift();
            tempShiftDto = new ShiftResponseDto(tempShift.getShiftId(), tempShift.getStartTime(), tempShift.getEndTime(), tempShift.getName());
        }

        DayDetailDto dayDetail = new DayDetailDto(
                changeRequest.getEffectiveDate().getDayOfWeek().name(),
                false,
                instructor.getFirstName() + " " + instructor.getLastName(),
                changeRequest.getDescription(),
                changeRequest.getEffectiveDate().getDayOfWeek().name(),
                room.getRoomId(),
                room.getRoomName(),
                room.getBuildingName(),
                changeRequest.getEffectiveDate(),
                tempShiftDto
        );

        String className = (changeRequest.getDescription() != null && !changeRequest.getDescription().isEmpty())
                ? changeRequest.getDescription()
                : "Booked by " + instructor.getFirstName() + " " + instructor.getLastName();

        return new ScheduleResponseDto(
                null, null, className, List.of(dayDetail), null, null,
                null, room.getRoomId(), room.getRoomName(), room.getBuildingName(),
                "Conference Room", false
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