//package org.example.roomschedulerapi.repository;
//
//import org.apache.ibatis.annotations.*;
//import org.example.roomschedulerapi.model.User;
//import java.util.List;
//
//@Mapper
//public interface UserRepository {
//
//    @Select("SELECT * FROM users WHERE user_id = #{userId}")
//    @Results(id = "userResultMap", value = {
//            @Result(property = "userId", column = "user_id"),
//            @Result(property = "email", column = "email"),
//            @Result(property = "passwordHash", column = "password_hash"),
//            @Result(property = "role", column = "role"),
//            @Result(property = "createdAt", column = "created_at")
//    })
//    User findById(Long userId);
//
//    @Select("SELECT * FROM users WHERE email = #{email}")
//    @ResultMap("userResultMap")
//    User findByEmail(String email);
//
//    @Select("SELECT * FROM users")
//    @ResultMap("userResultMap")
//    List<User> findAll();
//
//    @Insert("INSERT INTO users (email, password_hash, role) VALUES (#{email}, #{passwordHash}, #{role})")
//    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
//    int insert(User user);
//
//    @Update("UPDATE users SET email = #{email}, password_hash = #{passwordHash}, role = #{role} WHERE user_id = #{userId}")
//    int update(User user);
//
//    @Delete("DELETE FROM users WHERE user_id = #{userId}")
//    int deleteById(Long userId);
//
//    User save(User user);
//
//    boolean existsById(Long id);
//}

// FILE: org/example/mybatisdemo/repository/UserRepo.java
package org.example.roomschedulerapi.repository;

import org.apache.ibatis.annotations.*;
import org.example.roomschedulerapi.model.User;
import java.util.List;

@Mapper
public interface UserRepository {
    @Select("SELECT user_id, email, role, created_at FROM users") // Exclude password_hash by default for list
    List<User> findAll();

    @Select("SELECT user_id, email, role, created_at FROM users WHERE user_id = #{userId}")
    User findById(Integer userId);

    @Select("SELECT * FROM users WHERE email = #{email}") // For auth or internal checks
    User findByEmail(String email);

    // created_at has a DEFAULT CURRENT_TIMESTAMP, so it's optional in INSERT if DB handles it
    @Insert("INSERT INTO users(email, password_hash, role) VALUES(#{email}, #{passwordHash}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    void insert(User user);

    @Update("UPDATE users SET email=#{email}, password_hash=#{passwordHash}, role=#{role} WHERE user_id=#{userId}")
    void update(User user);

    @Update("UPDATE users SET email=#{email}, role=#{role} WHERE user_id=#{userId}")
    void updateProfile(User user); // Example: update without changing password

    @Delete("DELETE FROM users WHERE user_id=#{userId}")
    void delete(Integer userId);
}