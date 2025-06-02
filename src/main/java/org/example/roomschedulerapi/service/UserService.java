// FILE: org/example/mybatisdemo/service/UserService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Integer userId);
    User getUserByEmail(String email);
    User createUser(User user);
    User updateUser(Integer userId, User user);
    User partialUpdateUser(Integer userId, User userDetails);
    void deleteUser(Integer userId);
}