// FILE: org/example/mybatisdemo/controller/CourseController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.Course;
import org.example.roomschedulerapi.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Course Controller", description = "APIs for managing courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "Get all courses")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all courses")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved course"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Course> getCourseById(@Parameter(description = "ID of the course to retrieve") @PathVariable Integer id) {
        Course course = courseService.getCourseById(id);
        return (course != null) ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get courses by Department ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved courses for the department")
    public List<Course> getCoursesByDepartmentId(@Parameter(description = "ID of the department") @PathVariable Integer departmentId) {
        return courseService.getCoursesByDepartmentId(departmentId);
    }

    @PostMapping
    @Operation(summary = "Create a new course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created course"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., department not found, duplicate code)")
    })
    public ResponseEntity<Course> createCourse(@Parameter(description = "Course object to be created") @RequestBody Course course) {
        Course createdCourse = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated course"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Course> updateCourse(
            @Parameter(description = "ID of the course to update") @PathVariable Integer id,
            @Parameter(description = "Updated course object") @RequestBody Course courseDetails) {
        Course updatedCourse = courseService.updateCourse(id, courseDetails);
        return (updatedCourse != null) ? ResponseEntity.ok(updatedCourse) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an existing course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated course"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Course> partialUpdateCourse(
            @Parameter(description = "ID of the course to update") @PathVariable Integer id,
            @Parameter(description = "Course object with fields to update") @RequestBody Course courseDetails) {
        Course updatedCourse = courseService.partialUpdateCourse(id, courseDetails);
        return (updatedCourse != null) ? ResponseEntity.ok(updatedCourse) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted course"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., course is referenced by classes)")
    })
    public ResponseEntity<Void> deleteCourse(@Parameter(description = "ID of the course to delete") @PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}