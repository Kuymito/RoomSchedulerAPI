package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.*;
import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ChangeRequestCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ChangeRequestResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus; // <-- Make sure you have this enum
import org.example.roomschedulerapi.classroomscheduler.repository.*;
import org.example.roomschedulerapi.classroomscheduler.service.ChangeRequestService;
import org.example.roomschedulerapi.classroomscheduler.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final InstructorRepository instructorRepository;
    private final ClassRepository classRepository;
    private final RoomRepository roomRepository;
    private final ShiftRepository shiftRepository;
    private final ScheduleRepository scheduleRepository;
    private final NotificationService  notificationService;

    @Override
    @Transactional
    public ChangeRequestResponseDto createChangeRequest(ChangeRequestCreateDto requestDto, Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with ID: " + instructorId));

        Class aClass = classRepository.findById(requestDto.getClassId())
                .orElseThrow(() -> new NoSuchElementException("Class not found with ID: " + requestDto.getClassId()));

        Room room = roomRepository.findById(requestDto.getRoomId())
                .orElseThrow(() -> new NoSuchElementException("Room not found with ID: " + requestDto.getRoomId()));

        Shift shift = shiftRepository.findById(requestDto.getShiftId())
                .orElseThrow(() -> new NoSuchElementException("Shift not found with ID: " + requestDto.getShiftId()));

        // --- Business Logic for Creating the Request ---
        ChangeRequest changeRequest = new ChangeRequest();
        changeRequest.setInstructor(instructor);
        changeRequest.setAClass(aClass);
        changeRequest.setRoom(room);
        changeRequest.setShift(shift);
        changeRequest.setDescription(requestDto.getDescription());
        changeRequest.setDayOfChange(requestDto.getDayOfChange()); // Set the new date
        changeRequest.setStatus(RequestStatus.PENDING); // Default status

        ChangeRequest savedRequest = changeRequestRepository.save(changeRequest);

        // --- Notification Logic ---
        String instructorName = instructor.getFirstName() + " " + instructor.getLastName();
        notificationService.createNotificationForAdmins(savedRequest, "New change request from " + instructorName + " requires your approval.");
        notificationService.createNotificationForInstructor(instructor, savedRequest, "Your change request for class '" + aClass.getClassName() + "' has been submitted successfully.");

        return convertToDto(savedRequest);
    }

    @Override
    public List<ChangeRequestResponseDto> getAllChangeRequests() {
        return changeRequestRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChangeRequestResponseDto approveChangeRequest(Long requestId) {
        ChangeRequest changeRequest = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Change request not found with ID: " + requestId));

        changeRequest.setStatus(RequestStatus.APPROVED);

        // --- THIS IS THE FIX ---
        // Fetch a list of schedules for the class.
        List<Schedule> schedules = scheduleRepository.findAllByaClass_ClassId(changeRequest.getAClass().getClassId());

        // If there are schedules, update the first one. Otherwise, create a new one.
        Schedule scheduleToUpdate = schedules.isEmpty() ? new Schedule() : schedules.get(0);

        scheduleToUpdate.setAClass(changeRequest.getAClass());

        if (changeRequest.getRoom() != null) {
            scheduleToUpdate.setRoom(changeRequest.getRoom());
        }
        if (changeRequest.getShift() != null) {
            scheduleToUpdate.getAClass().setShift(changeRequest.getShift());
        }

        // Save the updated or new schedule, and the updated change request.
        scheduleRepository.save(scheduleToUpdate);
        changeRequestRepository.save(changeRequest);

        notificationService.createNotificationForInstructor(
                changeRequest.getInstructor(),
                changeRequest,
                "Your change request for class '" + changeRequest.getAClass().getClassName() + "' has been approved."
        );

        return convertToDto(changeRequest);
    }



    @Override
    @Transactional
    public ChangeRequestResponseDto denyChangeRequest(Long requestId) {
        ChangeRequest changeRequest = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Change request not found"));

        // CORRECT: Set status to DENIED
        changeRequest.setStatus(RequestStatus.DENIED);
        changeRequestRepository.save(changeRequest);

        // CORRECT: Send the "denied" notification HERE, not in the converter.
        notificationService.createNotificationForInstructor(changeRequest.getInstructor(), changeRequest, "Your change request for class '" + changeRequest.getAClass().getClassName() + "' has been denied.");

        return convertToDto(changeRequest);
    }

    private ChangeRequestResponseDto convertToDto(ChangeRequest changeRequest) {
        ChangeRequestResponseDto dto = new ChangeRequestResponseDto();
        dto.setRequestId(changeRequest.getRequestId());
        dto.setClassId(changeRequest.getAClass().getClassId());
        dto.setClassName(changeRequest.getAClass().getClassName());

        if (changeRequest.getRoom() != null) {
            dto.setNewRoomId(changeRequest.getRoom().getRoomId());
            dto.setNewRoomName(changeRequest.getRoom().getRoomName());
        }
        if (changeRequest.getShift() != null) {
            dto.setNewShiftId(changeRequest.getShift().getShiftId());
            dto.setNewShiftName(changeRequest.getShift().getName());
        }

        dto.setDescription(changeRequest.getDescription());
        dto.setDayOfChange(changeRequest.getDayOfChange()); // Include in the response DTO
        dto.setStatus(changeRequest.getStatus());
        dto.setRequestedAt(changeRequest.getRequestedAt());

        return dto;
    }


}