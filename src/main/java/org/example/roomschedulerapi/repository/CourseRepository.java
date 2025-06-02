// FILE: org/example/mybatisdemo/repository/CourseRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.Course;
import java.util.List;

@Mapper
public interface CourseRepository {
    @Select("SELECT * FROM courses")
    List<Course> findAll();

    @Select("SELECT * FROM courses WHERE course_id = #{courseId}")
    Course findById(Integer courseId);

    @Insert("INSERT INTO courses(department_id, name, code, credit_hours) VALUES(#{departmentId}, #{name}, #{code}, #{creditHours})")
    @Options(useGeneratedKeys = true, keyProperty = "courseId", keyColumn = "course_id")
    void insert(Course course);

    @Update("UPDATE courses SET department_id=#{departmentId}, name=#{name}, code=#{code}, credit_hours=#{creditHours} WHERE course_id=#{courseId}")
    void update(Course course);

    @Delete("DELETE FROM courses WHERE course_id=#{courseId}")
    void delete(Integer courseId);

    @Select("SELECT * FROM courses WHERE department_id = #{departmentId}")
    List<Course> findByDepartmentId(Integer departmentId);
}