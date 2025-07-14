package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.Admin;
import org.example.roomschedulerapi.classroomscheduler.model.dto.AdminResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.AdminUpdateDto;
import org.example.roomschedulerapi.classroomscheduler.repository.AdminRepository;
import org.example.roomschedulerapi.classroomscheduler.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Override
    public AdminResponseDto updateAdmin(Long id, AdminUpdateDto adminUpdateDto) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Admin with id " + id + " not found"));

        if (adminUpdateDto.getFirstName() != null) {
            admin.setFirstName(adminUpdateDto.getFirstName());
        }
        if (adminUpdateDto.getLastName() != null) {
            admin.setLastName(adminUpdateDto.getLastName());
        }
        if (adminUpdateDto.getEmail() != null) {
            admin.setEmail(adminUpdateDto.getEmail());
        }
        if (adminUpdateDto.getPhoneNumber() != null) {
            admin.setPhoneNumber(adminUpdateDto.getPhoneNumber());
        }
        if (adminUpdateDto.getAddress() != null) {
            admin.setAddress(adminUpdateDto.getAddress());
        }
        // --- NEW LOGIC ---
        if (adminUpdateDto.getProfile() != null) {
            admin.setProfile(adminUpdateDto.getProfile());
        }
        // --- END OF NEW LOGIC ---

        Admin updatedAdmin = adminRepository.save(admin);
        return convertToDto(updatedAdmin);
    }

    private AdminResponseDto convertToDto(Admin admin) {
        AdminResponseDto dto = new AdminResponseDto();
        dto.setAdminId(Math.toIntExact(admin.getAdminId()));
        dto.setFirstName(admin.getFirstName());
        dto.setLastName(admin.getLastName());
        dto.setEmail(admin.getEmail());
        dto.setUsername(admin.getUsername());
        dto.setPhoneNumber(admin.getPhoneNumber());
        dto.setAddress(admin.getAddress());

        // --- NEW LOGIC ---
        dto.setProfile(admin.getProfile());
        // --- END OF NEW LOGIC ---

        return dto;
    }
}