// FILE: org/example/mybatisdemo/controller/AdminController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.Admin;
import org.example.roomschedulerapi.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
@Tag(name = "Admin Controller", description = "APIs for managing admin records")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    @Operation(summary = "Get all admin records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all admin records")
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get admin record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved admin record"),
            @ApiResponse(responseCode = "404", description = "Admin record not found")
    })
    public ResponseEntity<Admin> getAdminById(@Parameter(description = "ID of the admin record to retrieve") @PathVariable Integer id) {
        Admin admin = adminService.getAdminById(id);
        return (admin != null) ? ResponseEntity.ok(admin) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get admin record by User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved admin record"),
            @ApiResponse(responseCode = "404", description = "Admin record not found for the given User ID")
    })
    public ResponseEntity<Admin> getAdminByUserId(@Parameter(description = "User ID associated with the admin record") @PathVariable Integer userId) {
        Admin admin = adminService.getAdminByUserId(userId);
        return (admin != null) ? ResponseEntity.ok(admin) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new admin record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created admin record"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., user_id does not exist or already an admin)")
    })
    public ResponseEntity<Admin> createAdmin(@Parameter(description = "Admin object to be created") @RequestBody Admin admin) {
        Admin createdAdmin = adminService.createAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing admin record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated admin record"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Admin record not found")
    })
    public ResponseEntity<Admin> updateAdmin(
            @Parameter(description = "ID of the admin record to update") @PathVariable Integer id,
            @Parameter(description = "Updated admin object") @RequestBody Admin adminDetails) {
        Admin updatedAdmin = adminService.updateAdmin(id, adminDetails);
        return (updatedAdmin != null) ? ResponseEntity.ok(updatedAdmin) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an existing admin record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated admin record"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Admin record not found")
    })
    public ResponseEntity<Admin> partialUpdateAdmin(
            @Parameter(description = "ID of the admin record to update") @PathVariable Integer id,
            @Parameter(description = "Admin object with fields to update") @RequestBody Admin adminDetails) {
        Admin updatedAdmin = adminService.partialUpdateAdmin(id, adminDetails);
        return (updatedAdmin != null) ? ResponseEntity.ok(updatedAdmin) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an admin record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted admin record"),
            @ApiResponse(responseCode = "404", description = "Admin record not found")
    })
    public ResponseEntity<Void> deleteAdmin(@Parameter(description = "ID of the admin record to delete") @PathVariable Integer id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}