// FILE: org/example/mybatisdemo/service/DepartmentService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();
    Department getDepartmentById(Integer departmentId);
    Department createDepartment(Department department);
    Department updateDepartment(Integer departmentId, Department departmentDetails);
    Department partialUpdateDepartment(Integer departmentId, Department departmentDetails);
    void deleteDepartment(Integer departmentId);
}