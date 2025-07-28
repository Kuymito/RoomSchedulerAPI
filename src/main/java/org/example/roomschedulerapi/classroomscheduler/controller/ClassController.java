    package org.example.roomschedulerapi.classroomscheduler.controller; // Adjust package

    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import jakarta.validation.Valid;
    import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
    import org.example.roomschedulerapi.classroomscheduler.model.dto.*;
    import org.example.roomschedulerapi.classroomscheduler.service.ClassService;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.Authentication;
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

        @PostMapping("/assign-instructor")
        public ResponseEntity<ClassResponseDto> assignInstructor(@RequestBody AssignInstructorDto assignInstructorDto) {
            ClassResponseDto updatedClass = classService.assignInstructor(assignInstructorDto);
            return ResponseEntity.ok(updatedClass);
        }

        @PostMapping("/unassign-instructor")
        public ResponseEntity<ApiResponse<ClassResponseDto>> unassignInstructor(
                @Valid @RequestBody UnassignInstructorDto unassignDto) {

            ClassResponseDto updatedClass = classService.unassignInstructor(unassignDto);

            ApiResponse<ClassResponseDto> response = new ApiResponse<>(
                    "Instructor unassigned successfully from " + unassignDto.getDayOfWeek(),
                    updatedClass,
                    HttpStatus.OK,
                    LocalDateTime.now()
            );
            return ResponseEntity.ok(response);
        }

        @GetMapping("/my-classes")
        public ResponseEntity<ApiResponse<List<ClassResponseDto>>> getMyAssignedClasses(Authentication authentication) {
            // 'authentication.getName()' will return the email of the logged-in user (from the JWT)
            String instructorEmail = authentication.getName();
            List<ClassResponseDto> classes = classService.getClassesForAuthenticatedInstructor(instructorEmail);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Assigned classes retrieved successfully", classes, HttpStatus.OK, LocalDateTime.now()
            ));
        }

        @GetMapping("/expired")
        @Operation(summary = "Get all classes or filter by expiration status")
        public ResponseEntity<ApiResponse<List<ClassResponseDto>>> getClasses(
                @Parameter(description = "Filter by expiration status (true or false). If omitted, returns all classes.")
                @RequestParam(required = false) Boolean expired
        ) {
            List<ClassResponseDto> classes;
            classes = classService.getClassesByExpirationStatus(expired);
            return ResponseEntity.ok(new ApiResponse<>("Classes retrieved successfully.", classes, HttpStatus.OK, LocalDateTime.now()));
        }

        @PostMapping("/swap-instructors-in-class")
        public ResponseEntity<ClassResponseDto> swapInstructorsInClass(@RequestBody SwapInstructorsInClassDto dto) {
            ClassResponseDto updatedClass = classService.swapInstructorsInClass(dto);
            return ResponseEntity.ok(updatedClass);
        }

        @PostMapping("/replace-instructor")
        public ResponseEntity<ClassResponseDto> replaceInstructor(@RequestBody ReplaceInstructorDto dto) {
            ClassResponseDto updatedClass = classService.replaceInstructor(dto);
            return ResponseEntity.ok(updatedClass);
        }

    }