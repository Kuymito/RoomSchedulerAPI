package org.example.roomschedulerapi.classroomscheduler.service; // Adjust to your package

import org.example.roomschedulerapi.classroomscheduler.model.dto.RoleResponseDto;


import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<RoleResponseDto> getAllRoles();

    Optional<RoleResponseDto> getRoleById(Long roleId);

    void deleteRole(Long roleId);
}