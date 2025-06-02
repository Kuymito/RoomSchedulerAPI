// FILE: org/example/mybatisdemo/service/InstructorDepartmentServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.InstructorDepartment;
import org.example.roomschedulerapi.repository.InstructorDepartmentRepository;
// You might also need InstructorRepo and DepartmentRepo to validate existence
import org.example.roomschedulerapi.repository.InstructorDepartmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InstructorDepartmentServiceImpl implements InstructorDepartmentService {

    private final InstructorDepartmentRepository instructorDepartmentRepo;

    public InstructorDepartmentServiceImpl(InstructorDepartmentRepository instructorDepartmentRepo) {
        this.instructorDepartmentRepo = instructorDepartmentRepo;
    }

    @Override
    public void addInstructorToDepartment(Integer instructorId, Integer departmentId) {
        // Optional: Check if instructor and department exist before inserting
        InstructorDepartment association = new InstructorDepartment(instructorId, departmentId);
        instructorDepartmentRepo.insert(association);
    }

    @Override
    public void removeInstructorFromDepartment(Integer instructorId, Integer departmentId) {
        instructorDepartmentRepo.delete(instructorId, departmentId);
    }

    @Override
    public List<InstructorDepartment> getDepartmentsForInstructor(Integer instructorId) {
        return instructorDepartmentRepo.findByInstructorId(instructorId);
        // If you want List<Department>, use the joined query from repo or map results
    }

    @Override
    public List<InstructorDepartment> getInstructorsForDepartment(Integer departmentId) {
        return instructorDepartmentRepo.findByDepartmentId(departmentId);
        // If you want List<Instructor>, use the joined query from repo or map results
    }

    @Override
    public List<InstructorDepartment> getAllAssociations() {
        return instructorDepartmentRepo.findAll();
    }
}