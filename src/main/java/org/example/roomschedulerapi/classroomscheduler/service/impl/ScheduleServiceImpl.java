    package org.example.roomschedulerapi.classroomscheduler.service.impl;

    import lombok.RequiredArgsConstructor;
    import org.example.roomschedulerapi.classroomscheduler.model.*;
    import org.example.roomschedulerapi.classroomscheduler.model.Class;
    import org.example.roomschedulerapi.classroomscheduler.model.dto.DayDetailDto;
    import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleRequestDto;
    import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleResponseDto;
    import org.example.roomschedulerapi.classroomscheduler.model.dto.ShiftResponseDto;
    import org.example.roomschedulerapi.classroomscheduler.repository.ClassRepository;
    import org.example.roomschedulerapi.classroomscheduler.repository.RoomRepository;
    import org.example.roomschedulerapi.classroomscheduler.repository.ScheduleRepository;
    import org.example.roomschedulerapi.classroomscheduler.service.ScheduleService;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.Collections;
    import java.util.List;
    import java.util.NoSuchElementException;
    import java.util.Optional;
    import java.util.stream.Collectors;
    import java.util.stream.Stream;

    @Service
    @RequiredArgsConstructor
    public class ScheduleServiceImpl implements ScheduleService {

        private final ScheduleRepository scheduleRepository;
        private final ClassRepository classRepository;
        private final RoomRepository roomRepository;


        private ScheduleResponseDto convertToDto(Schedule schedule) {
            Class aClass = schedule.getAClass();
            Room room = schedule.getRoom();
            Shift shiftEntity = aClass.getShift();

            // Create the Shift DTO
            ShiftResponseDto shiftDto = new ShiftResponseDto(
                    shiftEntity.getShiftId(),
                    shiftEntity.getStartTime(),
                    shiftEntity.getEndTime()
            );

            // **FIX**: Get all days from the classInstructors list and join them into a string.
            String days = Optional.ofNullable(aClass.getClassInstructors())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(ci -> ci.getDayOfWeek().name())
                    .collect(Collectors.joining(", "));

            List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                    .map(ci -> new DayDetailDto(ci.getDayOfWeek().name(), ci.isOnline()))
                    .collect(Collectors.toList());

            return new ScheduleResponseDto(
                    schedule.getScheduleId(),
                    aClass.getClassId(),
                    aClass.getClassName(),
                    dayDetails, // Use the new string of days
                    aClass.getGeneration(),
                    aClass.getSemester(),
                    shiftDto,
                    room.getRoomId(),
                    room.getRoomName(),
                    room.getBuildingName(),
                    aClass.getMajorName()
            );
        }


        @Override
        public List<ScheduleResponseDto> getAllClassesWithScheduleStatus() {
            List<org.example.roomschedulerapi.classroomscheduler.model.Class> allClasses = classRepository.findAll();

            return allClasses.stream()
                    .flatMap(aClass -> {
                        // This now returns a List, not an Optional
                        List<Schedule> schedules = scheduleRepository.findByaClass(aClass);

                        if (schedules.isEmpty()) {
                            // If there are no schedules, it's online/unassigned. Return a stream of one item.
                            return Stream.of(convertUnscheduledClassToDto(aClass));
                        } else {
                            // If there are one or more schedules, convert each one to a DTO.
                            return schedules.stream().map(this::convertToDto);
                        }
                    })
                    .collect(Collectors.toList());
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
            // 1. Validate class and room exist
            Class aClass = classRepository.findById(dto.getClassId())
                    .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + dto.getClassId()));

            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new NoSuchElementException("Room not found with id: " + dto.getRoomId()));

            // 2. Check if the class is already assigned to any room
            if (scheduleRepository.existsByaClass(aClass)) {
                throw new IllegalStateException("Class " + aClass.getClassName() + " is already assigned to a room");
            }

            // 3. Get all days this class meets
            List<DaysOfWeek> classDays = aClass.getClassInstructors().stream()
                    .map(ClassInstructor::getDayOfWeek)
                    .collect(Collectors.toList());

            if (classDays.isEmpty()) {
                throw new IllegalStateException("Class has no scheduled days");
            }

            // 4. Get the class's shift
            Shift classShift = aClass.getShift();
            if (classShift == null) {
                throw new IllegalStateException("Class has no assigned shift");
            }

            // 5. Check for room conflicts during the same time and days
            List<Schedule> conflictingSchedules = scheduleRepository.findConflictingSchedules(
                    room.getRoomId(),
                    classDays,
                    classShift.getStartTime(),
                    classShift.getEndTime()
            );

            if (!conflictingSchedules.isEmpty()) {
                String conflictingClasses = conflictingSchedules.stream()
                        .map(s -> s.getAClass().getClassName())
                        .collect(Collectors.joining(", "));

                throw new IllegalStateException(
                        "Room " + room.getRoomName() + " is already occupied by class(es): " +
                                conflictingClasses + " during the requested time"
                );
            }

            // 6. Create and save the new schedule if no conflicts
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
            // If it exists, delete it.
            scheduleRepository.deleteById(scheduleId);
        }


        @Override
        public List<ScheduleResponseDto> getSchedulesForInstructor(Long instructorId) {
            // **FIX**: Call the new, correct repository method.
            List<Schedule> schedules = scheduleRepository.findSchedulesByInstructorId(instructorId);

            return schedules.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        private ScheduleResponseDto convertUnscheduledClassToDto(org.example.roomschedulerapi.classroomscheduler.model.Class aClass) {
            Shift shiftEntity = aClass.getShift();

            ShiftResponseDto shiftDto = new ShiftResponseDto(
                    shiftEntity.getShiftId(),
                    shiftEntity.getStartTime(),
                    shiftEntity.getEndTime()
            );

            String days = Optional.ofNullable(aClass.getClassInstructors())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(ci -> ci.getDayOfWeek().name())
                    .collect(Collectors.joining(", "));

            // --- THE FIX ---
            // Check if ANY of the instructor assignments for this class are marked as online.
            boolean isClassOnline = aClass.getClassInstructors()
                    .stream()
                    .anyMatch(ClassInstructor::isOnline);

            String roomName = isClassOnline ? "Online" : "Unassigned";
            String buildingName = isClassOnline ? "N/A" : "TBD"; // TBD = To Be Determined

            List<DayDetailDto> dayDetails = aClass.getClassInstructors().stream()
                    .map(ci -> new DayDetailDto(ci.getDayOfWeek().name(), ci.isOnline()))
                    .collect(Collectors.toList());
            return new ScheduleResponseDto(
                    null, // No schedule ID
                    aClass.getClassId(),
                    aClass.getClassName(),
                    dayDetails,
                    aClass.getGeneration(),
                    aClass.getSemester(),
                    shiftDto,
                    null, // No room ID
                    roomName,
                    buildingName,
                    aClass.getMajorName()
            );
        }
    }