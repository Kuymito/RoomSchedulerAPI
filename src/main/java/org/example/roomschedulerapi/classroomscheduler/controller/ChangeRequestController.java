package org.example.roomschedulerapi.classroomscheduler.controller;

import jakarta.transaction.Transactional;
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

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/change-requests")
@RequiredArgsConstructor
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;

    @PostMapping
    @Transactional  // Add transaction management at controller level
    public ResponseEntity<ApiResponse<ChangeRequestResponseDto>> createChangeRequest(
            @Valid @RequestBody ChangeRequestCreateDto requestDto,
            @AuthenticationPrincipal Instructor instructor) throws AccessDeniedException {  // Directly use Instructor

        // Additional validation
        if (instructor == null) {
            throw new AccessDeniedException("Authentication required");
        }


        ChangeRequestResponseDto createdRequest = changeRequestService.createChangeRequest(requestDto, instructor);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        "Change request created successfully",
                        createdRequest,
                        HttpStatus.CREATED,
                        LocalDateTime.now()
                ));
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