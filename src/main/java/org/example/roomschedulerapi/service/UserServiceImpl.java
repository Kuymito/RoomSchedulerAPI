// FILE: org/example/mybatisdemo/service/UserServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.User;
import org.example.roomschedulerapi.repository.UserRepository;
// import org.springframework.security.crypto.password.PasswordEncoder; // For password hashing
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    // private final PasswordEncoder passwordEncoder; // Inject if using Spring Security

    public UserServiceImpl(UserRepository userRepo /*, PasswordEncoder passwordEncoder */) {
        this.userRepo = userRepo;
        // this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserById(Integer userId) {
        return userRepo.findById(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email); // Important: this returns password hash
    }

    @Override
    public User createUser(User user) {
        // user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash())); // Hash password before saving
        userRepo.insert(user);
        user.setPasswordHash(null); // Clear hash before returning
        return user;
    }

    @Override
    public User updateUser(Integer userId, User user) {
        User existingUser = userRepo.findById(userId);
        if (existingUser == null) {
            // throw new ResourceNotFoundException("User not found with id " + userId);
            return null;
        }
        user.setUserId(userId);
        // if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
        //     user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        // } else {
        //     user.setPasswordHash(existingUser.getPasswordHash()); // Keep old hash if not provided
        // }
        userRepo.update(user);
        user.setPasswordHash(null); // Clear hash before returning
        return user;
    }

    @Override
    public User partialUpdateUser(Integer userId, User userDetails) {
        User existingUser = userRepo.findById(userId); // This might fetch the hash
        if (existingUser == null) {
            // throw new ResourceNotFoundException("User not found with id " + userId);
            return null;
        }
        boolean changed = false;
        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
            changed = true;
        }
        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
            changed = true;
        }
        // Handle password update separately and carefully
        // if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
        //    existingUser.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
        //    changed = true;
        // }

        if (changed) {
            // If only profile fields changed (no password), a different repo method might be better
            userRepo.updateProfile(existingUser); // Assuming updateProfile doesn't touch password
        }
        existingUser.setPasswordHash(null); // Clear hash before returning
        return existingUser;
    }


    @Override
    public void deleteUser(Integer userId) {
        userRepo.delete(userId);
    }
}