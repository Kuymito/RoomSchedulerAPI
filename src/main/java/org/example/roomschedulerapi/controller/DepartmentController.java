// FILE: org/example/mybatisdemo/controller/DepartmentController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.Department;
import org.example.roomschedulerapi.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@Tag(name = "Department Controller", description = "APIs for managing departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @Operation(summary = "Get all departments")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all departments")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved department"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<Department> getDepartmentById(@Parameter(description = "ID of the department to retrieve") @PathVariable Integer id) {
        Department department = departmentService.getDepartmentById(id);
        return (department != null) ? ResponseEntity.ok(department) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new department")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created department"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., duplicate name/code)")
    })
    public ResponseEntity<Department> createDepartment(@Parameter(description = "Department object to be created") @RequestBody Department department) {
        Department createdDepartment = departmentService.createDepartment(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing department")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated department"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<Department> updateDepartment(
            @Parameter(description = "ID of the department to update") @PathVariable Integer id,
            @Parameter(description = "Updated department object") @RequestBody Department departmentDetails) {
        Department updatedDepartment = departmentService.updateDepartment(id, departmentDetails);
        return (updatedDepartment != null) ? ResponseEntity.ok(updatedDepartment) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an existing department")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated department"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<Department> partialUpdateDepartment(
            @Parameter(description = "ID of the department to update") @PathVariable Integer id,
            @Parameter(description = "Department object with fields to update") @RequestBody Department departmentDetails) {
        Department updatedDepartment = departmentService.partialUpdateDepartment(id, departmentDetails);
        return (updatedDepartment != null) ? ResponseEntity.ok(updatedDepartment) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted department"),
            @ApiResponse(responseCode = "404", description = "Department not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., department is referenced by other entities)")
    })
    public ResponseEntity<Void> deleteDepartment(@Parameter(description = "ID of the department to delete") @PathVariable Integer id) {
        // Consider adding try-catch for DataIntegrityViolationException if deletion causes FK constraint errors
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}