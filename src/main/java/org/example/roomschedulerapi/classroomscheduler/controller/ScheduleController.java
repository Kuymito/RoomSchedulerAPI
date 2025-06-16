package org.example.roomschedulerapi.classroomscheduler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleResponseDto;
import org.example.roomschedulerapi.classroomscheduler.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleResponseDto>>> getAllSchedules() {
        List<ScheduleResponseDto> schedules = scheduleService.getAllSchedules();
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


}