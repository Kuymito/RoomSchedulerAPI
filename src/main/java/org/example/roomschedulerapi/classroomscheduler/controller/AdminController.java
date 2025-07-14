package org.example.roomschedulerapi.classroomscheduler.controller;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.dto.AdminResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.AdminUpdateDto;
import org.example.roomschedulerapi.classroomscheduler.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/{id}")
    public ResponseEntity<AdminResponseDto> updateAdminProfile(@PathVariable Long id, @RequestBody AdminUpdateDto adminUpdateDto) {
        return ResponseEntity.ok(adminService.updateAdmin(id, adminUpdateDto));
    }
}