// FILE: org/example/mybatisdemo/service/DepartmentServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Department;
import org.example.roomschedulerapi.repository.DepartmentRepository;
// import org.example.mybatisdemo.exception.ResourceNotFoundException;
import org.example.roomschedulerapi.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepo;

    public DepartmentServiceImpl(DepartmentRepository departmentRepo) {
        this.departmentRepo = departmentRepo;
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepo.findAll();
    }

    @Override
    public Department getDepartmentById(Integer departmentId) {
        Department department = departmentRepo.findById(departmentId);
        // if (department == null) {
        //     throw new ResourceNotFoundException("Department not found with id: " + departmentId);
        // }
        return department;
    }

    @Override
    public Department createDepartment(Department department) {
        // Add validation if needed (e.g., unique name/code)
        departmentRepo.insert(department);
        return department;
    }

    @Override
    public Department updateDepartment(Integer departmentId, Department departmentDetails) {
        Department existingDepartment = departmentRepo.findById(departmentId);
        if (existingDepartment == null) {
            // throw new ResourceNotFoundException("Department not found with id: " + departmentId);
            return null;
        }
        departmentDetails.setDepartmentId(departmentId);
        departmentRepo.update(departmentDetails);
        return departmentDetails;
    }

    @Override
    public Department partialUpdateDepartment(Integer departmentId, Department departmentDetails) {
        Department existingDepartment = departmentRepo.findById(departmentId);
        if (existingDepartment == null) {
            // throw new ResourceNotFoundException("Department not found with id: " + departmentId);
            return null;
        }

        boolean updated = false;
        if (departmentDetails.getName() != null) {
            existingDepartment.setName(departmentDetails.getName());
            updated = true;
        }
        if (departmentDetails.getCode() != null) {
            existingDepartment.setCode(departmentDetails.getCode());
            updated = true;
        }

        if (updated) {
            departmentRepo.update(existingDepartment);
        }
        return existingDepartment;
    }

    @Override
    public void deleteDepartment(Integer departmentId) {
        Department existingDepartment = departmentRepo.findById(departmentId);
        if (existingDepartment == null) {
            // throw new ResourceNotFoundException("Department not found with id: " + departmentId);
            return;
        }
        // Consider referential integrity: what happens if courses or instructors reference this dept?
        departmentRepo.delete(departmentId);
    }
}