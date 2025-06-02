// FILE: org/example/mybatisdemo/repository/RoomRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.Room;
import java.util.List;

@Mapper
public interface RoomRepository {
    @Select("SELECT * FROM rooms")
    List<Room> findAll();

    @Select("SELECT * FROM rooms WHERE room_id = #{roomId}")
    Room findById(Integer roomId);

    @Insert("INSERT INTO rooms(building_id, number, capacity, type) VALUES(#{buildingId}, #{number}, #{capacity}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "roomId", keyColumn = "room_id")
    void insert(Room room);

    @Update("UPDATE rooms SET building_id=#{buildingId}, number=#{number}, capacity=#{capacity}, type=#{type} WHERE room_id=#{roomId}")
    void update(Room room);

    @Delete("DELETE FROM rooms WHERE room_id=#{roomId}")
    void delete(Integer roomId);

    @Select("SELECT * FROM rooms WHERE building_id = #{buildingId}")
    List<Room> findByBuildingId(Integer buildingId);
}