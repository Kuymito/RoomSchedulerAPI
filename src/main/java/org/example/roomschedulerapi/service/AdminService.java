// FILE: org/example/mybatisdemo/service/AdminService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Admin;
import java.util.List;

public interface AdminService {
    List<Admin> getAllAdmins();
    Admin getAdminById(Integer adminId);
    Admin getAdminByUserId(Integer userId);
    Admin createAdmin(Admin admin);
    Admin updateAdmin(Integer adminId, Admin adminDetails);
    // Partial update might be trivial for Admin as it only has userId, which is unlikely to change post-creation
    // without deleting and recreating. But included for consistency.
    Admin partialUpdateAdmin(Integer adminId, Admin adminDetails);
    void deleteAdmin(Integer adminId);
}