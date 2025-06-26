package org.example.roomschedulerapi.classroomscheduler.service.impl;

import org.example.roomschedulerapi.classroomscheduler.model.Department;
import org.example.roomschedulerapi.classroomscheduler.model.dto.DepartmentDto;
import org.example.roomschedulerapi.classroomscheduler.repository.DepartmentRepository;
import org.example.roomschedulerapi.classroomscheduler.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    // Helper to convert Entity to DTO
    private DepartmentDto convertToDto(Department department) {
        return new DepartmentDto(department.getDepartmentId(), department.getName());
    }

    @Override
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DepartmentDto> getDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId).map(this::convertToDto);
    }

    @Override
    @Transactional
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        Department department = new Department();
        department.setName(departmentDto.getName());
        Department savedDepartment = departmentRepository.save(department);
        return convertToDto(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long departmentId, DepartmentDto departmentDto) {
        Department existingDepartment = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NoSuchElementException("Department not found with id: " + departmentId));

        existingDepartment.setName(departmentDto.getName());
        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return convertToDto(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new NoSuchElementException("Cannot delete. Department not found with id: " + departmentId);
        }
        // Consider checking for related classes before deleting
        // to prevent DataIntegrityViolationException if there's a constraint
        departmentRepository.deleteById(departmentId);
    }
}