package org.example.roomschedulerapi.classroomscheduler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.Admin;
import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ChangeRequestCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ChangeRequestResponseDto;
import org.example.roomschedulerapi.classroomscheduler.service.ChangeRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/change-requests")
@RequiredArgsConstructor
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChangeRequestResponseDto>> createChangeRequest(
            @Valid @RequestBody ChangeRequestCreateDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long instructorId;

        // Check the role of the logged-in user
        if (userDetails instanceof Instructor) {
            // If the user is an instructor, use their own ID.
            instructorId = ((Instructor) userDetails).getInstructorId();
        } else if (userDetails instanceof Admin) {
            // If the user is an admin, they MUST provide the instructorId in the request.
            instructorId = requestDto.getInstructorId();
            if (instructorId == null) {
                throw new IllegalArgumentException("Admin must provide an instructorId to create a change request.");
            }
        } else {
            // Handle other cases or throw an error
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(
                    "User role not supported for this action.", null, HttpStatus.FORBIDDEN, LocalDateTime.now()));
        }

        ChangeRequestResponseDto createdRequest = changeRequestService.createChangeRequest(requestDto, instructorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                "Change request created successfully", createdRequest, HttpStatus.CREATED, LocalDateTime.now()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChangeRequestResponseDto>>> getAllChangeRequests() {
        List<ChangeRequestResponseDto> requests = changeRequestService.getAllChangeRequests();
        return ResponseEntity.ok(new ApiResponse<>("Change requests retrieved successfully", requests, HttpStatus.OK, LocalDateTime.now()));
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<ApiResponse<ChangeRequestResponseDto>> approveChangeRequest(@PathVariable Long requestId) {
        ChangeRequestResponseDto updatedRequest = changeRequestService.approveChangeRequest(requestId);
        return ResponseEntity.ok(new ApiResponse<>("Change request approved", updatedRequest, HttpStatus.OK, LocalDateTime.now()));
    }

    @PostMapping("/{requestId}/deny")
    public ResponseEntity<ApiResponse<ChangeRequestResponseDto>> denyChangeRequest(@PathVariable Long requestId) {
        ChangeRequestResponseDto updatedRequest = changeRequestService.denyChangeRequest(requestId);
        return ResponseEntity.ok(new ApiResponse<>("Change request denied", updatedRequest, HttpStatus.OK, LocalDateTime.now()));
    }
}