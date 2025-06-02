// FILE: org/example/mybatisdemo/repository/BuildingRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.Building;
import java.util.List;

@Mapper
public interface BuildingRepository {
    @Select("SELECT * FROM buildings")
    List<Building> findAll();

    @Select("SELECT * FROM buildings WHERE building_id = #{buildingId}")
    Building findById(Integer buildingId);

    @Insert("INSERT INTO buildings(name, code) VALUES(#{name}, #{code})")
    @Options(useGeneratedKeys = true, keyProperty = "buildingId", keyColumn = "building_id")
    void insert(Building building);

    @Update("UPDATE buildings SET name=#{name}, code=#{code} WHERE building_id=#{buildingId}")
    void update(Building building);

    @Delete("DELETE FROM buildings WHERE building_id=#{buildingId}")
    void delete(Integer buildingId);
}