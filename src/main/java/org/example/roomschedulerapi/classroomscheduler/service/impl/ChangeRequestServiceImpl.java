package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.*;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ChangeRequestCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ChangeRequestResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;
import org.example.roomschedulerapi.classroomscheduler.repository.*;
import org.example.roomschedulerapi.classroomscheduler.service.ChangeRequestService;
import org.example.roomschedulerapi.classroomscheduler.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final ScheduleRepository scheduleRepository;
    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;
    private final ShiftRepository shiftRepository;
    private final NotificationService notificationService;

    @Override
    public ChangeRequestResponseDto createChangeRequest(ChangeRequestCreateDto requestDto, Instructor instructor) {
        Room tempRoom = roomRepository.findById(requestDto.getNewRoomId())
                .orElseThrow(() -> new NoSuchElementException("Temporary room not found with id: " + requestDto.getNewRoomId()));

        Shift shift = null;
        if (requestDto.getShiftId() != null) {
            shift = shiftRepository.findById(requestDto.getShiftId())
                    .orElseThrow(() -> new NoSuchElementException("Shift not found with id: " + requestDto.getShiftId()));
        }

        // --- VALIDATION LOGIC ---
        if (requestDto.getScheduleId() != null) {
            List<ChangeRequest> existingApprovedRequests = changeRequestRepository.findApprovedChangeRequestsForSchedule(requestDto.getScheduleId());
            if (!existingApprovedRequests.isEmpty()) {
                throw new IllegalStateException("This class already has an approved temporary schedule change. Another request cannot be submitted.");
            }
        }

        // Check for overlapping requests before creating a new one.
        if (shift != null) {
            List<RequestStatus> conflictingStatuses = List.of(RequestStatus.PENDING, RequestStatus.APPROVED);
            boolean isOverlapping = changeRequestRepository.existsByTemporaryRoomAndEffectiveDateAndShiftAndStatusIn(
                    tempRoom,
                    requestDto.getEffectiveDate(),
                    shift,
                    conflictingStatuses
            );

            if (isOverlapping) {
                throw new IllegalStateException(
                        "This request cannot be submitted because a conflicting request for the same room, date, and time shift already exists."
                );
            }
        }

        ChangeRequest newRequest = new ChangeRequest();
        newRequest.setRequestingInstructor(instructor);
        newRequest.setTemporaryRoom(tempRoom);
        newRequest.setEffectiveDate(requestDto.getEffectiveDate());
        newRequest.setDescription(requestDto.getDescription());
        newRequest.setStatus(RequestStatus.PENDING);
        newRequest.setRequestedAt(OffsetDateTime.now());
        newRequest.setShift(shift);

        String adminMessage;
        String instructorMessage;

        // Formatter for a more readable date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        String formattedDate = newRequest.getEffectiveDate().format(dateFormatter);

        // If scheduleId is provided, it's a class change request
        if (requestDto.getScheduleId() != null) {
            Schedule schedule = scheduleRepository.findById(requestDto.getScheduleId())
                    .orElseThrow(() -> new NoSuchElementException("Schedule not found with id: " + requestDto.getScheduleId()));
            newRequest.setOriginalSchedule(schedule);

            // UPDATED ADMIN MESSAGE
            adminMessage = String.format(
                    "Request from %s %s: Move class '%s' to room '%s' on %s (%s).",
                    instructor.getFirstName(),
                    instructor.getLastName(),
                    schedule.getAClass().getClassName(),
                    tempRoom.getRoomName(),
                    formattedDate,
                    shift != null ? shift.getName() : "N/A"
            );

            instructorMessage = String.format(
                    "Your request to move class '%s' to room '%s' on %s has been submitted.",
                    schedule.getAClass().getClassName(),
                    tempRoom.getRoomName(),
                    formattedDate
            );
        } else {
            // If scheduleId is null, it's a conference room booking
            // UPDATED ADMIN MESSAGE
            adminMessage = String.format(
                    "Booking request from %s %s: Room '%s' on %s (%s).",
                    instructor.getFirstName(),
                    instructor.getLastName(),
                    tempRoom.getRoomName(),
                    formattedDate,
                    shift != null ? shift.getName() : "N/A"
            );

            instructorMessage = String.format(
                    "Your request to book conference room '%s' on %s has been submitted.",
                    tempRoom.getRoomName(),
                    formattedDate
            );
        }

        ChangeRequest savedRequest = changeRequestRepository.save(newRequest);

        // --- NOTIFICATION LOGIC ---
        notificationService.createNotificationForAdmins(savedRequest, adminMessage);
        notificationService.createNotificationForInstructor(instructor.getInstructorId(), savedRequest, instructorMessage);

        return convertToDto(savedRequest);
    }


    @Override
    public List<ChangeRequestResponseDto> getAllChangeRequests() {
        return changeRequestRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChangeRequestResponseDto approveChangeRequest(Long requestId) {
        ChangeRequest request = findRequestById(requestId);
        request.setStatus(RequestStatus.APPROVED);
        ChangeRequest savedRequest = changeRequestRepository.save(request);

        String message;
        if (savedRequest.getOriginalSchedule() != null) {
            message = String.format(
                    "Your request for class '%s' on %s for room %s has been APPROVED.",
                    savedRequest.getOriginalSchedule().getAClass().getClassName(),
                    savedRequest.getEffectiveDate(),
                    savedRequest.getTemporaryRoom().getRoomName() // FIX: Use getRoomName()
            );
        } else {
            message = String.format(
                    "Your conference room booking for room '%s' on %s has been APPROVED.",
                    savedRequest.getTemporaryRoom().getRoomName(),
                    savedRequest.getEffectiveDate()
            );
        }
        notificationService.createNotificationForInstructor(savedRequest.getRequestingInstructor().getInstructorId(), savedRequest, message);

        return convertToDto(savedRequest);
    }

    @Override
    public ChangeRequestResponseDto denyChangeRequest(Long requestId) {
        ChangeRequest request = findRequestById(requestId);
        request.setStatus(RequestStatus.DENIED);
        ChangeRequest savedRequest = changeRequestRepository.save(request);

        String message;
        if (savedRequest.getOriginalSchedule() != null) {
            message = String.format(
                    "Your request for class '%s' on %s for room %s has been DENIED.",
                    savedRequest.getOriginalSchedule().getAClass().getClassName(),
                    savedRequest.getEffectiveDate(),
                    savedRequest.getTemporaryRoom().getRoomName() // FIX: Use getRoomName()
            );
        } else {
            message = String.format(
                    "Your conference room booking for room '%s' on %s has been DENIED.",
                    savedRequest.getTemporaryRoom().getRoomName(),
                    savedRequest.getEffectiveDate()
            );
        }
        notificationService.createNotificationForInstructor(savedRequest.getRequestingInstructor().getInstructorId(), savedRequest, message);

        return convertToDto(savedRequest);
    }

    private ChangeRequest findRequestById(Long requestId) {
        return changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("ChangeRequest not found with id: " + requestId));
    }

    private ChangeRequestResponseDto convertToDto(ChangeRequest request) {
        ChangeRequestResponseDto dto = new ChangeRequestResponseDto();
        dto.setId(request.getId());
        dto.setStatus(request.getStatus());
        dto.setDescription(request.getDescription());
        dto.setEffectiveDate(request.getEffectiveDate());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setRequestingInstructorName(request.getRequestingInstructor().getFirstName() + " " + request.getRequestingInstructor().getLastName());
        dto.setTemporaryRoomName(request.getTemporaryRoom().getRoomName());

        if (request.getShift() != null) {
            dto.setShiftName(request.getShift().getName());
        }

        if (request.getOriginalSchedule() != null) {
            dto.setScheduleId(request.getOriginalSchedule().getScheduleId());
            dto.setClassName(request.getOriginalSchedule().getAClass().getClassName());
            dto.setOriginalRoomName(request.getOriginalSchedule().getRoom().getRoomName());
        } else {
            dto.setClassName("Conference Room Booking");
        }

        return dto;
    }
}