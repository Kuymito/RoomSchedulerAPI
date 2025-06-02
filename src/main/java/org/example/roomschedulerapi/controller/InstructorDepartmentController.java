// FILE: org/example/mybatisdemo/controller/InstructorDepartmentController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.InstructorDepartment;
import org.example.roomschedulerapi.service.InstructorDepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instructor-departments")
@Tag(name = "Instructor-Department", description = "APIs for managing instructor-department relationships")
public class InstructorDepartmentController {

    private final InstructorDepartmentService instructorDepartmentService;

    public InstructorDepartmentController(InstructorDepartmentService instructorDepartmentService) {
        this.instructorDepartmentService = instructorDepartmentService;
    }

    @PostMapping
    @Operation(summary = "Associate an instructor with a department")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Association created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or IDs not found"),
            @ApiResponse(responseCode = "409", description = "Association already exists") // Conflict
    })
    public ResponseEntity<Void> addInstructorToDepartment(@RequestBody InstructorDepartment association) {
        // Add try-catch for potential DataIntegrityViolationException if association already exists or FK constraint fails
        try {
            instructorDepartmentService.addInstructorToDepartment(association.getInstructorId(), association.getDepartmentId());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) { // More specific exception handling needed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Or CONFLICT
        }
    }

    @DeleteMapping("/instructor/{instructorId}/department/{departmentId}")
    @Operation(summary = "Remove an association between an instructor and a department")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Association removed successfully"),
            @ApiResponse(responseCode = "404", description = "Association not found")
    })
    public ResponseEntity<Void> removeInstructorFromDepartment(@PathVariable Integer instructorId, @PathVariable Integer departmentId) {
        instructorDepartmentService.removeInstructorFromDepartment(instructorId, departmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/instructor/{instructorId}")
    @Operation(summary = "Get all department associations for a specific instructor")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved associations")
    public List<InstructorDepartment> getDepartmentsForInstructor(@PathVariable Integer instructorId) {
        return instructorDepartmentService.getDepartmentsForInstructor(instructorId);
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get all instructor associations for a specific department")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved associations")
    public List<InstructorDepartment> getInstructorsForDepartment(@PathVariable Integer departmentId) {
        return instructorDepartmentService.getInstructorsForDepartment(departmentId);
    }

    @GetMapping
    @Operation(summary = "Get all instructor-department associations")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all associations")
    public List<InstructorDepartment> getAllAssociations() {
        return instructorDepartmentService.getAllAssociations();
    }
}