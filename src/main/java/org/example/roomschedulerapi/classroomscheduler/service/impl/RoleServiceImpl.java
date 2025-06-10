package org.example.roomschedulerapi.classroomscheduler.service.impl; // Adjust to your package

import org.example.roomschedulerapi.classroomscheduler.model.Role;
import org.example.roomschedulerapi.classroomscheduler.model.dto.RoleResponseDto;
import org.example.roomschedulerapi.classroomscheduler.repository.RoleRepository;
import org.example.roomschedulerapi.classroomscheduler.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private RoleResponseDto convertToDto(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleResponseDto(role.getRoleId(), role.getRoleName());
    }

    @Override
    public List<RoleResponseDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RoleResponseDto> getRoleById(Long roleId) {
        return roleRepository.findById(roleId).map(this::convertToDto);
    }


    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new NoSuchElementException("Role not found with id: " + roleId);
        }
        try {
            roleRepository.deleteById(roleId);
        } catch (DataIntegrityViolationException e) {
            // This exception is typically thrown if the role is still assigned to instructors
            // due to the ON DELETE RESTRICT foreign key constraint.
            throw new DataIntegrityViolationException(
                    "Cannot delete role with ID " + roleId + " as it is currently assigned to one or more instructors."
            );
        }
    }
}