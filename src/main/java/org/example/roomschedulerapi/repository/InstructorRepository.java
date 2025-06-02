// FILE: org/example/mybatisdemo/repository/InstructorRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.Instructor;
import java.util.List;

@Mapper
public interface InstructorRepository {
    @Select("SELECT * FROM instructor")
    List<Instructor> findAll();

    @Select("SELECT * FROM instructor WHERE instructor_id = #{instructorId}")
    Instructor findById(Integer instructorId);

    @Select("SELECT * FROM instructor WHERE user_id = #{userId}")
    Instructor findByUserId(Integer userId);

    @Insert("INSERT INTO instructor(user_id, name, phone, degree, address) VALUES(#{userId}, #{name}, #{phone}, #{degree}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "instructorId", keyColumn = "instructor_id")
    void insert(Instructor instructor);

    @Update("UPDATE instructor SET user_id=#{userId}, name=#{name}, phone=#{phone}, degree=#{degree}, address=#{address} WHERE instructor_id=#{instructorId}")
    void update(Instructor instructor);

    @Delete("DELETE FROM instructor WHERE instructor_id=#{instructorId}")
    void delete(Integer instructorId);
}