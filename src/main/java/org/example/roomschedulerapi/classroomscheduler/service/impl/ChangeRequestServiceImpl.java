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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final ScheduleRepository scheduleRepository;
    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;

    @Override
    public ChangeRequestResponseDto createChangeRequest(ChangeRequestCreateDto requestDto, Instructor instructor) {
        Schedule schedule = scheduleRepository.findById(requestDto.getScheduleId())
                .orElseThrow(() -> new NoSuchElementException("Schedule not found with id: " + requestDto.getScheduleId()));
        Room tempRoom = roomRepository.findById(requestDto.getNewRoomId())
                .orElseThrow(() -> new NoSuchElementException("Temporary room not found with id: " + requestDto.getNewRoomId()));

        ChangeRequest newRequest = new ChangeRequest();
        newRequest.setRequestingInstructor(instructor);
        newRequest.setOriginalSchedule(schedule);
        newRequest.setTemporaryRoom(tempRoom);
        newRequest.setEffectiveDate(requestDto.getEffectiveDate());
        newRequest.setDescription(requestDto.getDescription());
        newRequest.setStatus(RequestStatus.PENDING);
        newRequest.setRequestedAt(OffsetDateTime.now());

        ChangeRequest savedRequest = changeRequestRepository.save(newRequest);

        // --- NOTIFICATION LOGIC ---

        // 1. Notify Admins
        String adminMessage = String.format(
                "New request from %s %s for class '%s' requires your approval.",
                instructor.getFirstName(),
                instructor.getLastName(),
                schedule.getAClass().getClassName()
        );
        notificationService.createNotificationForAdmins(savedRequest, adminMessage);

        // 2. Notify Instructor
        String instructorMessage = String.format(
                "Your request to move class '%s' to room '%s' on %s has been submitted.",
                schedule.getAClass().getClassName(),
                tempRoom.getRoomName(),
                savedRequest.getEffectiveDate()
        );
        // FIX: Pass the instructor's ID instead of the detached object.
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

        // --- ADD NOTIFICATION LOGIC ---
        String message = String.format(
                "Your request for class '%s' on %s has been APPROVED.",
                savedRequest.getOriginalSchedule().getAClass().getClassName(),
                savedRequest.getEffectiveDate()
        );
        notificationService.createNotificationForInstructor(savedRequest.getId(), savedRequest, message);

        return convertToDto(savedRequest);
    }

    @Override
    public ChangeRequestResponseDto denyChangeRequest(Long requestId) {
        ChangeRequest request = findRequestById(requestId);
        request.setStatus(RequestStatus.DENIED);
        ChangeRequest savedRequest = changeRequestRepository.save(request);

        // --- ADD NOTIFICATION LOGIC ---
        String message = String.format(
                "Your request for class '%s' on %s has been DENIED.",
                savedRequest.getOriginalSchedule().getAClass().getClassName(),
                savedRequest.getEffectiveDate()
        );
        notificationService.createNotificationForInstructor(savedRequest.getId(), savedRequest, message);

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
        dto.setScheduleId(request.getOriginalSchedule().getScheduleId());
        dto.setClassName(request.getOriginalSchedule().getAClass().getClassName());
        dto.setRequestingInstructorName(request.getRequestingInstructor().getFirstName() + " " + request.getRequestingInstructor().getLastName());
        dto.setOriginalRoomName(request.getOriginalSchedule().getRoom().getRoomName());
        dto.setTemporaryRoomName(request.getTemporaryRoom().getRoomName());
        return dto;
    }
}