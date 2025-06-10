package org.example.roomschedulerapi.classroomscheduler.controller; // Adjust package

import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ClassCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ClassResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ClassUpdateDto;
import org.example.roomschedulerapi.classroomscheduler.service.ClassService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/class")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassResponseDto>>> getAllClasses( // Changed return type
                                                                              @RequestParam(required = false) Boolean isArchived) {
        List<ClassResponseDto> classes = classService.getAllClasses(isArchived);
        return ResponseEntity.ok(new ApiResponse<>(
                "Classes retrieved successfully", classes, HttpStatus.OK, LocalDateTime.now()
        ));
    }

    @GetMapping("/{classId}")
    public ResponseEntity<ApiResponse<ClassResponseDto>> getClassById(@PathVariable Long classId) { // Changed return type
        return classService.getClassById(classId)
                .map(aClassDto -> ResponseEntity.ok(new ApiResponse<>( // Changed variable name
                        "Class retrieved successfully", aClassDto, HttpStatus.OK, LocalDateTime.now())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                        "Class not found with id: " + classId, null, HttpStatus.NOT_FOUND, LocalDateTime.now())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClassResponseDto>> createClass(@RequestBody ClassCreateDto classCreateDto) { // Changed return type
        try {
            ClassResponseDto createdClass = classService.createClass(classCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                    "Class created successfully", createdClass, HttpStatus.CREATED, LocalDateTime.now()
            ));
        } catch (NoSuchElementException e) { // If related entities like Shift, Department, Instructor are not found by ID
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Error creating class: " + e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()
            ));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Error creating class: " + e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()
            ));
        }
    }

    @PutMapping("/{classId}")
    public ResponseEntity<ApiResponse<ClassResponseDto>> updateClass(@PathVariable Long classId, @RequestBody ClassCreateDto classDetailsDto) { // Changed return type
        try {
            ClassResponseDto updatedClass = classService.updateClass(classId, classDetailsDto);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Class updated successfully", updatedClass, HttpStatus.OK, LocalDateTime.now()
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Error updating class: " + e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()
            ));
        }
    }

    @PatchMapping("/{classId}")
    public ResponseEntity<ApiResponse<ClassResponseDto>> patchClass(@PathVariable Long classId, @RequestBody ClassUpdateDto classUpdateDto) { // Changed return type
        try {
            ClassResponseDto patchedClass = classService.patchClass(classId, classUpdateDto);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Class patched successfully", patchedClass, HttpStatus.OK, LocalDateTime.now()
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Error patching class: " + e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()
            ));
        }
    }

    @PatchMapping("/{classId}/archive")
    public ResponseEntity<ApiResponse<ClassResponseDto>> archiveClass(@PathVariable Long classId, @RequestBody Map<String, Boolean> payload) { // Changed return type
        Boolean archiveStatus = payload.get("is_archived");
        if (archiveStatus == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "'is_archived' field (true/false) is required in payload.", null, HttpStatus.BAD_REQUEST, LocalDateTime.now()
            ));
        }
        try {
            ClassResponseDto aClassDto = classService.archiveClass(classId, archiveStatus); // Changed variable name
            String action = archiveStatus ? "archived" : "unarchived";
            return ResponseEntity.ok(new ApiResponse<>(
                    "Class " + action + " successfully", aClassDto, HttpStatus.OK, LocalDateTime.now()
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        }
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable Long classId) {
        try {
            classService.deleteClass(classId);
            return ResponseEntity.ok(new ApiResponse<Void>(
                    "Class deleted successfully", null, HttpStatus.OK, LocalDateTime.now()
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Error deleting class: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now()
            ));
        }
    }
}