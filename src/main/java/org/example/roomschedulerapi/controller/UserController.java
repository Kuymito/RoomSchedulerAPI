//package org.example.roomschedulerapi.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//// import io.swagger.v3.oas.annotations.Parameter; // Not used directly on method params here
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import org.example.roomschedulerapi.model.User;
//import org.example.roomschedulerapi.service.UserService;
//// import org.example.roomschedulerapi.exception.ResourceNotFoundException; // For try-catch approach
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//    private final UserService userService;
//
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping
//    @Operation(summary = "Get all users")
//    @ApiResponse(responseCode = "200", description = "Successfully retrieved all users")
//    public ResponseEntity<List<User>> getAllUsers() {
//        return ResponseEntity.ok(userService.getAllUsers());
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "Get user by ID")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    public ResponseEntity<User> getUserById(@PathVariable Long id) {
//        User user = userService.getUserById(id);
//        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
//    }
//
//    @PostMapping
//    @Operation(summary = "Create a new user")
//    @ApiResponse(responseCode = "201", description = "User created successfully")
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        User createdUser = userService.createUser(user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
//    }
//
//    @PutMapping("/{id}")
//    @Operation(summary = "Update a user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
//        User updatedUser = userService.updateUser(id, user);
//        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
//    }
//
//    @PatchMapping("/{id}")
//    @Operation(summary = "Partially update a user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    public ResponseEntity<User> partialUpdateUser(@PathVariable Long id, @RequestBody User user) {
//        User updatedUser = userService.partialUpdateUser(id, user);
//        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
//    }
//
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT) // Spring will set 204 if method completes without exception
//    @Operation(summary = "Delete a user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//            // This 404 will be achieved if userService.deleteUser throws ResourceNotFoundException
//            // and a global exception handler maps it to HttpStatus.NOT_FOUND.
//    })
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id); // If user not found, ResourceNotFoundException is thrown by service.
//        // This needs to be handled by an @ControllerAdvice for 404.
//        return ResponseEntity.noContent().build(); // Only reached if deleteUser doesn't throw.
//        // @ResponseStatus(HttpStatus.NO_CONTENT) makes this redundant if method is void.
//    }
//}

// FILE: org/example/mybatisdemo/controller/UserController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.User;
import org.example.roomschedulerapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created user"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return (updatedUser != null) ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> partialUpdateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        User updatedUser = userService.partialUpdateUser(id, userDetails);
        return (updatedUser != null) ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "404", description = "User not found (or operation is idempotent)")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}