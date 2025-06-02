// FILE: org/example/mybatisdemo/service/InstructorDepartmentService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.InstructorDepartment;
import java.util.List;

public interface InstructorDepartmentService {
    void addInstructorToDepartment(Integer instructorId, Integer departmentId);
    void removeInstructorFromDepartment(Integer instructorId, Integer departmentId);
    List<InstructorDepartment> getDepartmentsForInstructor(Integer instructorId);
    List<InstructorDepartment> getInstructorsForDepartment(Integer departmentId);
    List<InstructorDepartment> getAllAssociations();
}