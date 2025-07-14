package org.example.roomschedulerapi.classroomscheduler.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.exception.ResourceNotFoundException;
import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleSwapDto;
import org.example.roomschedulerapi.classroomscheduler.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleResponseDto>>> getAllSchedules() {
        List<ScheduleResponseDto> schedules = scheduleService.getAllClassesWithScheduleStatus();
        ApiResponse<List<ScheduleResponseDto>> response = new ApiResponse<>(
                "All schedules retrieved successfully",
                schedules,
                HttpStatus.OK,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }



    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> assignRoom(
            @Valid @RequestBody ScheduleRequestDto scheduleRequestDto) {
        try {
            ScheduleResponseDto createdSchedule = scheduleService.assignRoomToClass(scheduleRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                    "Room successfully assigned to class.",
                    createdSchedule,
                    HttpStatus.CREATED,
                    LocalDateTime.now()
            ));
        } catch (NoSuchElementException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Failed to assign room: " + e.getMessage(),
                    null,
                    HttpStatus.BAD_REQUEST,
                    LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "An unexpected error occurred: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    LocalDateTime.now()
            ));
        }
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.unassignRoomFromClass(scheduleId);
        return ResponseEntity.noContent().build(); // Standard practice for successful DELETE
    }

    @GetMapping("/my-schedule")
    public ResponseEntity<ApiResponse<List<ScheduleResponseDto>>> getMySchedules(
            // FIX 1: Change the parameter type from 'User' to 'Instructor'
            @AuthenticationPrincipal Instructor instructor) {

        // FIX 2: Call the correct getter method
        Long instructorId = instructor.getInstructorId();

        List<ScheduleResponseDto> schedules = scheduleService.getSchedulesForInstructor(instructorId);

        ApiResponse<List<ScheduleResponseDto>> response = new ApiResponse<>(
                "Schedules for the current instructor retrieved successfully",
                schedules,
                HttpStatus.OK,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')") // Secures the endpoint for Admins only
    public ResponseEntity<ApiResponse<List<ScheduleResponseDto>>> getSchedulesByInstructorId(@PathVariable Long instructorId) {
        List<ScheduleResponseDto> schedules = scheduleService.getSchedulesForInstructor(instructorId);

        ApiResponse<List<ScheduleResponseDto>> response = new ApiResponse<>(
                "Schedules for instructor " + instructorId + " retrieved successfully",
                schedules,
                HttpStatus.OK,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/swap")
    @Operation(summary = "Swap the rooms of two existing schedules.")
    public ResponseEntity<ApiResponse<Void>> swapTwoSchedules(@RequestBody ScheduleSwapDto swapDto) {
        try {
            scheduleService.swapSchedules(swapDto);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            "Schedules swapped successfully.",
                            null,
                            HttpStatus.OK,
                            LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            // Catches exceptions like ResourceNotFoundException from the service
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(
                            e.getMessage(),
                            null,
                            HttpStatus.OK,
                            LocalDateTime.now()
                    )
            );
        }
    }

    @PutMapping("/{scheduleId}/move")
    @Operation(summary = "Move a schedule to a new room.")
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> moveSchedule(
            @PathVariable Long scheduleId,
            @RequestBody Map<String, Long> payload) {

        Long newRoomId = payload.get("newRoomId");
        if (newRoomId == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("newRoomId is required.", null, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        }

        try {
            ScheduleResponseDto updatedSchedule = scheduleService.moveSchedule(scheduleId, newRoomId);
            return ResponseEntity.ok(new ApiResponse<>("Schedule moved successfully.", updatedSchedule, HttpStatus.OK, LocalDateTime.now()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        }
    }

}