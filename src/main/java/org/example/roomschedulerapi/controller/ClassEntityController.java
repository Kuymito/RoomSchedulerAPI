// FILE: org/example/roomschedulerapi/controller/ClassEntityController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.ClassEntity; // Updated model import
import org.example.roomschedulerapi.service.ClassEntityService; // Updated service import
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classes") // Endpoint remains the same as per previous context
@Tag(name = "Class Controller", description = "APIs for managing academic classes")
public class ClassEntityController {

    private final ClassEntityService classEntityService; // Updated service type

    // Constructor name updated to match class name and service type
    public ClassEntityController(ClassEntityService classEntityService) {
        this.classEntityService = classEntityService;
    }

    @GetMapping
    @Operation(summary = "Get all classes") // Summary updated for clarity
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all classes")
    public List<ClassEntity> getAllClassEntities() { // Method name and return type updated
        return classEntityService.getAllClassEntities(); // Service method updated
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get class by ID") // Summary updated
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved class"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    public ResponseEntity<ClassEntity> getClassEntityById(@Parameter(description = "ID of the class") @PathVariable Integer id) { // Method name and return type updated
        ClassEntity classEntity = classEntityService.getClassEntityById(id); // Service method and variable type updated
        return (classEntity != null) ? ResponseEntity.ok(classEntity) : ResponseEntity.notFound().build();
    }

    @GetMapping("/instructor/{instructorId}")
    @Operation(summary = "Get classes by Instructor ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved classes for the instructor")
    public List<ClassEntity> getClassesByInstructorId(@Parameter(description = "ID of the instructor") @PathVariable Integer instructorId) { // Return type updated
        return classEntityService.getClassEntitiesByInstructorId(instructorId); // Service method updated
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get classes by Course ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved classes for the course")
    public List<ClassEntity> getClassesByCourseId(@Parameter(description = "ID of the course") @PathVariable Integer courseId) { // Return type updated
        return classEntityService.getClassEntitiesByCourseId(courseId); // Service method updated
    }

    @PostMapping
    @Operation(summary = "Create a new class") // Summary updated
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created class"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., foreign keys not found, scheduling conflict)")
    })
    public ResponseEntity<ClassEntity> createClassEntity(@Parameter(description = "Class object to be created") @RequestBody ClassEntity classEntity) { // Method name, parameter type, and return type updated
        // Add validation for FKs and potentially for scheduling conflicts in the service layer
        ClassEntity createdClass = classEntityService.createClassEntity(classEntity); // Service method and variable type updated
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClass);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing class") // Summary updated
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated class"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    public ResponseEntity<ClassEntity> updateClassEntity( // Method name and return type updated
                                                          @Parameter(description = "ID of the class to update") @PathVariable Integer id,
                                                          @Parameter(description = "Updated class object") @RequestBody ClassEntity classDetails) { // Parameter type updated
        ClassEntity updatedClass = classEntityService.updateClassEntity(id, classDetails); // Service method and variable type updated
        return (updatedClass != null) ? ResponseEntity.ok(updatedClass) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an existing class") // Summary updated
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated class"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    public ResponseEntity<ClassEntity> partialUpdateClassEntity( // Method name and return type updated
                                                                 @Parameter(description = "ID of the class to update") @PathVariable Integer id,
                                                                 @Parameter(description = "Class object with fields to update") @RequestBody ClassEntity classDetails) { // Parameter type updated
        ClassEntity updatedClass = classEntityService.partialUpdateClassEntity(id, classDetails); // Service method and variable type updated
        return (updatedClass != null) ? ResponseEntity.ok(updatedClass) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/archive") // Changed endpoint to be more specific for archiving
    @Operation(summary = "Archive a class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully archived class"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    public ResponseEntity<Void> archiveClassEntity(@Parameter(description = "ID of the class to archive") @PathVariable Integer id) { // Method name updated
        classEntityService.archiveClassEntity(id); // Service method updated
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a class permanently")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted class"),
            @ApiResponse(responseCode = "404", description = "Class not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., class has active student enrollments or requests)")
    })
    public ResponseEntity<Void> deleteClassEntity(@Parameter(description = "ID of the class to delete") @PathVariable Integer id) { // Method name updated
        classEntityService.deleteClassEntity(id); // Service method updated
        return ResponseEntity.noContent().build();
    }
}