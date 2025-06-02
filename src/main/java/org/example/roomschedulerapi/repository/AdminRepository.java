// FILE: org/example/mybatisdemo/repository/AdminRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.Admin;
import java.util.List;

@Mapper
public interface AdminRepository {
    @Select("SELECT * FROM admin")
    List<Admin> findAll();

    @Select("SELECT * FROM admin WHERE admin_id = #{adminId}")
    Admin findById(Integer adminId);

    @Select("SELECT * FROM admin WHERE user_id = #{userId}")
    Admin findByUserId(Integer userId);

    @Insert("INSERT INTO admin(user_id) VALUES(#{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "adminId", keyColumn = "admin_id")
    void insert(Admin admin);

    @Update("UPDATE admin SET user_id=#{userId} WHERE admin_id=#{adminId}")
    void update(Admin admin);

    @Delete("DELETE FROM admin WHERE admin_id=#{adminId}")
    void delete(Integer adminId);
}