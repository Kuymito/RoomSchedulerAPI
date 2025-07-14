package org.example.roomschedulerapi.classroomscheduler.service;

import org.example.roomschedulerapi.classroomscheduler.model.dto.AdminResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.AdminUpdateDto;

public interface AdminService {
    AdminResponseDto updateAdmin(Long id, AdminUpdateDto adminUpdateDto);
}
