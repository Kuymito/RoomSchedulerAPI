// FILE: org/example/mybatisdemo/repository/ChangeRequestRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.ChangeRequest;
import java.util.List;

@Mapper
public interface ChangeRequestRepository {
    @Select("SELECT * FROM request")
    List<ChangeRequest> findAll();

    @Select("SELECT * FROM request WHERE request_id = #{requestId}")
    ChangeRequest findById(Integer requestId);

    // requested_at and status have defaults, so they can be omitted in insert if DB handles them.
    // But it's safer to set them in code or ensure the DTO/model has them before insert.
    @Insert("INSERT INTO request(instructor_id, class_id, reason, preferred_room, status) " +
            "VALUES(#{instructorId}, #{classId}, #{reason}, #{preferredRoomId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "requestId", keyColumn = "request_id")
    void insert(ChangeRequest changeRequest);

    @Update("UPDATE request SET instructor_id=#{instructorId}, class_id=#{classId}, reason=#{reason}, " +
            "preferred_room=#{preferredRoomId}, status=#{status} WHERE request_id=#{requestId}")
    void update(ChangeRequest changeRequest);

    @Delete("DELETE FROM request WHERE request_id=#{requestId}")
    void delete(Integer requestId);

    @Select("SELECT * FROM request WHERE instructor_id = #{instructorId}")
    List<ChangeRequest> findByInstructorId(Integer instructorId);

    @Select("SELECT * FROM request WHERE class_id = #{classId}")
    List<ChangeRequest> findByClassId(Integer classId);
}