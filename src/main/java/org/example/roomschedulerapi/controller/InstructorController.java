// FILE: org/example/mybatisdemo/controller/InstructorController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.Instructor;
import org.example.roomschedulerapi.service.InstructorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instructors")
@Tag(name = "Instructor Controller", description = "APIs for managing instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    @Operation(summary = "Get all instructors")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all instructors")
    public List<Instructor> getAllInstructors() {
        return instructorService.getAllInstructors();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get instructor by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved instructor"),
            @ApiResponse(responseCode = "404", description = "Instructor not found")
    })
    public ResponseEntity<Instructor> getInstructorById(@Parameter(description = "ID of the instructor to retrieve") @PathVariable Integer id) {
        Instructor instructor = instructorService.getInstructorById(id);
        return (instructor != null) ? ResponseEntity.ok(instructor) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get instructor by User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved instructor"),
            @ApiResponse(responseCode = "404", description = "Instructor not found for the given User ID")
    })
    public ResponseEntity<Instructor> getInstructorByUserId(@Parameter(description = "User ID associated with the instructor") @PathVariable Integer userId) {
        Instructor instructor = instructorService.getInstructorByUserId(userId);
        return (instructor != null) ? ResponseEntity.ok(instructor) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new instructor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created instructor"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., user_id not found or already an instructor)")
    })
    public ResponseEntity<Instructor> createInstructor(@Parameter(description = "Instructor object to be created") @RequestBody Instructor instructor) {
        Instructor createdInstructor = instructorService.createInstructor(instructor);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInstructor);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing instructor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated instructor"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Instructor not found")
    })
    public ResponseEntity<Instructor> updateInstructor(
            @Parameter(description = "ID of the instructor to update") @PathVariable Integer id,
            @Parameter(description = "Updated instructor object") @RequestBody Instructor instructorDetails) {
        Instructor updatedInstructor = instructorService.updateInstructor(id, instructorDetails);
        return (updatedInstructor != null) ? ResponseEntity.ok(updatedInstructor) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an existing instructor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated instructor"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Instructor not found")
    })
    public ResponseEntity<Instructor> partialUpdateInstructor(
            @Parameter(description = "ID of the instructor to update") @PathVariable Integer id,
            @Parameter(description = "Instructor object with fields to update") @RequestBody Instructor instructorDetails) {
        Instructor updatedInstructor = instructorService.partialUpdateInstructor(id, instructorDetails);
        return (updatedInstructor != null) ? ResponseEntity.ok(updatedInstructor) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an instructor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted instructor"),
            @ApiResponse(responseCode = "404", description = "Instructor not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., instructor has active classes or requests)")
    })
    public ResponseEntity<Void> deleteInstructor(@Parameter(description = "ID of the instructor to delete") @PathVariable Integer id) {
        // Service layer should handle referential integrity checks before deletion
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }
}