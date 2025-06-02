// FILE: org/example/mybatisdemo/repository/AcademicClassRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.ClassEntity;
import org.example.roomschedulerapi.model.ClassEntity;

import java.util.List;

@Mapper
public interface ClassEntityRepository {
    @Select("SELECT * FROM classes")
    List<ClassEntity> findAll();

    @Select("SELECT * FROM classes WHERE class_id = #{classId}")
    ClassEntity findById(Integer classId);

    @Insert("INSERT INTO classes(instructor_id, course_id, room_id, timeslot_id, max_students, start_date, end_date, status, is_archived) " +
            "VALUES(#{instructorId}, #{courseId}, #{roomId}, #{timeslotId}, #{maxStudents}, #{startDate}, #{endDate}, #{status}, #{isArchived})")
    @Options(useGeneratedKeys = true, keyProperty = "classId", keyColumn = "class_id")
    void insert(ClassEntity academicClass);

    @Update("UPDATE classes SET instructor_id=#{instructorId}, course_id=#{courseId}, room_id=#{roomId}, timeslot_id=#{timeslotId}, " +
            "max_students=#{maxStudents}, start_date=#{startDate}, end_date=#{endDate}, status=#{status}, is_archived=#{isArchived} " +
            "WHERE class_id=#{classId}")
    void update(ClassEntity academicClass);

    @Delete("DELETE FROM classes WHERE class_id=#{classId}") // Or an update to set is_archived=true
    void delete(Integer classId);

    @Select("SELECT * FROM classes WHERE instructor_id = #{instructorId}")
    List<ClassEntity> findByInstructorId(Integer instructorId);

    @Select("SELECT * FROM classes WHERE course_id = #{courseId}")
    List<ClassEntity> findByCourseId(Integer courseId);

    @Update("UPDATE classes SET is_archived = true, status = 'Archived' WHERE class_id = #{classId}")
    void archive(Integer classId);

    ClassEntity save(ClassEntity classEntity);

    void deleteById(Integer classId);
}