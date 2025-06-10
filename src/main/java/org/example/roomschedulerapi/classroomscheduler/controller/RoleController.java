package org.example.roomschedulerapi.classroomscheduler.controller; // Adjust to your package

import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse; // Assuming you have this
import org.example.roomschedulerapi.classroomscheduler.model.dto.RoleResponseDto;
import org.example.roomschedulerapi.classroomscheduler.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponseDto>>> getAllRoles() {
        List<RoleResponseDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(new ApiResponse<>("Roles retrieved successfully", roles, HttpStatus.OK, LocalDateTime.now()));
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<RoleResponseDto>> getRoleById(@PathVariable Long roleId) {
        return roleService.getRoleById(roleId)
                .map(dto -> ResponseEntity.ok(new ApiResponse<>("Role retrieved successfully", dto, HttpStatus.OK, LocalDateTime.now())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Role not found with id: " + roleId, null, HttpStatus.NOT_FOUND, LocalDateTime.now())));
    }


    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long roleId) {
        try {
            roleService.deleteRole(roleId);
            return ResponseEntity.ok(new ApiResponse<Void>("Role deleted successfully", null, HttpStatus.OK, LocalDateTime.now()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict is appropriate here
                    .body(new ApiResponse<>(e.getMessage(), null, HttpStatus.CONFLICT, LocalDateTime.now()));
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error deleting role: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now()));
        }
    }
}