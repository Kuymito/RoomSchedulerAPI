package org.example.roomschedulerapi.classroomscheduler.service;

import org.example.roomschedulerapi.classroomscheduler.model.dto.DepartmentDto;
import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    List<DepartmentDto> getAllDepartments();
    Optional<DepartmentDto> getDepartmentById(Long departmentId);
    DepartmentDto createDepartment(DepartmentDto departmentDto);
    DepartmentDto updateDepartment(Long departmentId, DepartmentDto departmentDto);
    void deleteDepartment(Long departmentId);
}