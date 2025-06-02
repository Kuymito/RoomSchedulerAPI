// FILE: org/example/mybatisdemo/repository/DepartmentRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.Department;
import java.util.List;

@Mapper
public interface DepartmentRepository {
    @Select("SELECT * FROM department")
    List<Department> findAll();

    @Select("SELECT * FROM department WHERE department_id = #{departmentId}")
    Department findById(Integer departmentId);

    @Insert("INSERT INTO department(name, code) VALUES(#{name}, #{code})")
    @Options(useGeneratedKeys = true, keyProperty = "departmentId", keyColumn = "department_id")
    void insert(Department department);

    @Update("UPDATE department SET name=#{name}, code=#{code} WHERE department_id=#{departmentId}")
    void update(Department department);

    @Delete("DELETE FROM department WHERE department_id=#{departmentId}")
    void delete(Integer departmentId);
}