package org.example.roomschedulerapi.classroomscheduler.controller;

import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.dto.DepartmentDto;
import org.example.roomschedulerapi.classroomscheduler.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/department")
// Add CORS configuration here if it's not configured globally in your application
// @CrossOrigin(origins = "http://localhost:3000")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        ApiResponse<List<DepartmentDto>> response = new ApiResponse<>(
                "Departments retrieved successfully", departments, HttpStatus.OK, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDto>> getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id)
                .map(dto -> ResponseEntity.ok(new ApiResponse<>("Department retrieved successfully", dto, HttpStatus.OK, LocalDateTime.now())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Department not found with id: " + id, null, HttpStatus.NOT_FOUND, LocalDateTime.now())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDto>> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        try {
            DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Department created successfully", createdDepartment, HttpStatus.CREATED, LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("Error creating department: " + e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDto>> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentDto departmentDto) {
        try {
            DepartmentDto updatedDepartment = departmentService.updateDepartment(id, departmentDto);
            return ResponseEntity.ok(new ApiResponse<>("Department updated successfully", updatedDepartment, HttpStatus.OK, LocalDateTime.now()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("Error updating department: " + e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok(new ApiResponse<Void>("Department deleted successfully", null, HttpStatus.OK, LocalDateTime.now()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        } catch (Exception e) { // Catch DataIntegrityViolationException if department is in use
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Error deleting department: It may be in use by a class or instructor.", null, HttpStatus.CONFLICT, LocalDateTime.now()));
        }
    }
}