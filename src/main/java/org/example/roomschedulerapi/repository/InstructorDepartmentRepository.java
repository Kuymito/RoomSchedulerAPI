// FILE: org/example/mybatisdemo/repository/InstructorDepartmentRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.InstructorDepartment;
// Potentially import Department and Instructor if returning those directly from joins
import java.util.List;

@Mapper
public interface InstructorDepartmentRepository {

    @Insert("INSERT INTO instructor_department(instructor_id, department_id) VALUES(#{instructorId}, #{departmentId})")
    void insert(InstructorDepartment instructorDepartment);

    @Delete("DELETE FROM instructor_department WHERE instructor_id = #{instructorId} AND department_id = #{departmentId}")
    void delete(@Param("instructorId") Integer instructorId, @Param("departmentId") Integer departmentId);

    @Select("SELECT * FROM instructor_department WHERE instructor_id = #{instructorId}")
    List<InstructorDepartment> findByInstructorId(Integer instructorId);

    @Select("SELECT * FROM instructor_department WHERE department_id = #{departmentId}")
    List<InstructorDepartment> findByDepartmentId(Integer departmentId);

    @Select("SELECT * FROM instructor_department")
    List<InstructorDepartment> findAll();

    // Example of a join to get departments for an instructor
    // @Select("SELECT d.* FROM department d JOIN instructor_department id ON d.department_id = id.department_id WHERE id.instructor_id = #{instructorId}")
    // List<org.example.mybatisdemo.model.Department> findDepartmentsForInstructor(Integer instructorId);

    // Example of a join to get instructors for a department
    // @Select("SELECT i.* FROM instructor i JOIN instructor_department id ON i.instructor_id = id.instructor_id WHERE id.department_id = #{departmentId}")
    // List<org.example.mybatisdemo.model.Instructor> findInstructorsForDepartment(Integer departmentId);
}