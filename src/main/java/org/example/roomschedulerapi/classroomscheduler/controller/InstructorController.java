package org.example.roomschedulerapi.classroomscheduler.controller; // Adjust to your package

import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse; // Assuming you have this
import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorUpdateDto;
import org.example.roomschedulerapi.classroomscheduler.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    @Autowired
    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InstructorResponseDto>>> getAllInstructors(
            @RequestParam(required = false) Boolean isArchived) {
        List<InstructorResponseDto> instructors = instructorService.getAllInstructors(isArchived);
        return ResponseEntity.ok(new ApiResponse<>("Instructors retrieved successfully", instructors, HttpStatus.OK, LocalDateTime.now()));
    }

    @GetMapping("/{instructorId}")
    public ResponseEntity<ApiResponse<InstructorResponseDto>> getInstructorById(@PathVariable Long instructorId) {
        return instructorService.getInstructorById(instructorId)
                .map(dto -> ResponseEntity.ok(new ApiResponse<>("Instructor retrieved successfully", dto, HttpStatus.OK, LocalDateTime.now())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Instructor not found with id: " + instructorId, null, HttpStatus.NOT_FOUND, LocalDateTime.now())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InstructorResponseDto>> createInstructor(@RequestBody /* @Valid */ InstructorCreateDto createDto) {
        try {
            InstructorResponseDto createdInstructor = instructorService.createInstructor(createDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Instructor created successfully", createdInstructor, HttpStatus.CREATED, LocalDateTime.now()));
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred while creating the instructor.", null, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now()));
        }
    }

    @PutMapping("/{instructorId}")
    public ResponseEntity<ApiResponse<InstructorResponseDto>> updateInstructor(@PathVariable Long instructorId, @RequestBody /* @Valid */ InstructorUpdateDto updateDto) {
        try {
            InstructorResponseDto updatedInstructor = instructorService.updateInstructor(instructorId, updateDto);
            return ResponseEntity.ok(new ApiResponse<>("Instructor updated successfully", updatedInstructor, HttpStatus.OK, LocalDateTime.now()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred while updating the instructor.", null, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now()));
        }
    }

    @PatchMapping("/{instructorId}/archive")
    public ResponseEntity<ApiResponse<InstructorResponseDto>> archiveInstructor(@PathVariable Long instructorId, @RequestBody Map<String, Boolean> payload) {
        Boolean archiveStatus = payload.get("is_archived");
        if (archiveStatus == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("'is_archived' field (true/false) is required in payload.", null, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        }
        try {
            InstructorResponseDto instructorDto = instructorService.archiveInstructor(instructorId, archiveStatus);
            String action = archiveStatus ? "archived" : "unarchived";
            return ResponseEntity.ok(new ApiResponse<>("Instructor " + action + " successfully", instructorDto, HttpStatus.OK, LocalDateTime.now()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        }
    }

    @DeleteMapping("/{instructorId}")
    public ResponseEntity<ApiResponse<Void>> deleteInstructor(@PathVariable Long instructorId) {
        try {
            instructorService.deleteInstructor(instructorId);
            return ResponseEntity.ok(new ApiResponse<Void>("Instructor deleted successfully", null, HttpStatus.OK, LocalDateTime.now()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        } catch (Exception e) { // Handle other exceptions like DataIntegrityViolationException if instructor is linked
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error deleting instructor: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now()));
        }
    }
}