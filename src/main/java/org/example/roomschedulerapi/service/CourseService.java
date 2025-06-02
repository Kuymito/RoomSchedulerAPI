// FILE: org/example/mybatisdemo/service/CourseService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Course;
import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    Course getCourseById(Integer courseId);
    List<Course> getCoursesByDepartmentId(Integer departmentId);
    Course createCourse(Course course);
    Course updateCourse(Integer courseId, Course courseDetails);
    Course partialUpdateCourse(Integer courseId, Course courseDetails);
    void deleteCourse(Integer courseId);
}