// FILE: org/example/mybatisdemo/service/AdminServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Admin;
import org.example.roomschedulerapi.repository.AdminRepository;
// import org.example.mybatisdemo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepo;

    public AdminServiceImpl(AdminRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepo.findAll();
    }

    @Override
    public Admin getAdminById(Integer adminId) {
        Admin admin = adminRepo.findById(adminId);
        // if (admin == null) {
        //     throw new ResourceNotFoundException("Admin not found with id: " + adminId);
        // }
        return admin;
    }

    @Override
    public Admin getAdminByUserId(Integer userId) {
        Admin admin = adminRepo.findByUserId(userId);
        // if (admin == null) {
        //     throw new ResourceNotFoundException("Admin not found for user id: " + userId);
        // }
        return admin;
    }

    @Override
    public Admin createAdmin(Admin admin) {
        // Add validation if needed (e.g., check if user_id exists and is valid and not already an admin)
        adminRepo.insert(admin);
        return admin;
    }

    @Override
    public Admin updateAdmin(Integer adminId, Admin adminDetails) {
        Admin existingAdmin = adminRepo.findById(adminId);
        if (existingAdmin == null) {
            // throw new ResourceNotFoundException("Admin not found with id: " + adminId);
            return null;
        }
        adminDetails.setAdminId(adminId);
        adminRepo.update(adminDetails);
        return adminDetails;
    }

    @Override
    public Admin partialUpdateAdmin(Integer adminId, Admin adminDetails) {
        Admin existingAdmin = adminRepo.findById(adminId);
        if (existingAdmin == null) {
            // throw new ResourceNotFoundException("Admin not found with id: " + adminId);
            return null;
        }
        // User ID is the only field other than PK. Usually, changing user_id for an admin
        // means deleting the old admin record and creating a new one if user_id is unique.
        // If it's allowed to change, then:
        if (adminDetails.getUserId() != null) {
            existingAdmin.setUserId(adminDetails.getUserId());
            adminRepo.update(existingAdmin);
        }
        return existingAdmin;
    }

    @Override
    public void deleteAdmin(Integer adminId) {
        Admin existingAdmin = adminRepo.findById(adminId);
        if (existingAdmin == null) {
            // throw new ResourceNotFoundException("Admin not found with id: " + adminId);
            return;
        }
        adminRepo.delete(adminId);
    }
}