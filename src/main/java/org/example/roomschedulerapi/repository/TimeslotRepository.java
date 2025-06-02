// FILE: org/example/mybatisdemo/repository/TimeSlotRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.Timeslot;
import java.util.List;

@Mapper
public interface TimeslotRepository {
    @Select("SELECT * FROM time_slots")
    List<Timeslot> findAll();

    @Select("SELECT * FROM time_slots WHERE timeslot_id = #{timeslotId}")
    Timeslot findById(Integer timeslotId);

    @Insert("INSERT INTO time_slots(day, start_time, end_time, is_weekend, schedule_type) " +
            "VALUES(#{day}, #{startTime}, #{endTime}, #{isWeekend}, #{scheduleType})")
    @Options(useGeneratedKeys = true, keyProperty = "timeslotId", keyColumn = "timeslot_id")
    void insert(Timeslot timeSlot);

    @Update("UPDATE time_slots SET day=#{day}, start_time=#{startTime}, end_time=#{endTime}, " +
            "is_weekend=#{isWeekend}, schedule_type=#{scheduleType} WHERE timeslot_id=#{timeslotId}")
    void update(Timeslot timeSlot);

    @Delete("DELETE FROM time_slots WHERE timeslot_id=#{timeslotId}")
    void delete(Integer timeslotId);
}