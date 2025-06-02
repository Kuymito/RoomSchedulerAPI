// FILE: org/example/mybatisdemo/service/CourseServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Course;
import org.example.roomschedulerapi.repository.CourseRepository;
// import org.example.mybatisdemo.exception.ResourceNotFoundException;
import org.example.roomschedulerapi.repository.CourseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepo;

    public CourseServiceImpl(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    @Override
    public Course getCourseById(Integer courseId) {
        Course course = courseRepo.findById(courseId);
        // if (course == null) {
        //     throw new ResourceNotFoundException("Course not found with id: " + courseId);
        // }
        return course;
    }

    @Override
    public List<Course> getCoursesByDepartmentId(Integer departmentId) {
        return courseRepo.findByDepartmentId(departmentId);
    }

    @Override
    public Course createCourse(Course course) {
        // Add validation if needed (e.g., department_id exists, unique code within department)
        courseRepo.insert(course);
        return course;
    }

    @Override
    public Course updateCourse(Integer courseId, Course courseDetails) {
        Course existingCourse = courseRepo.findById(courseId);
        if (existingCourse == null) {
            // throw new ResourceNotFoundException("Course not found with id: " + courseId);
            return null;
        }
        courseDetails.setCourseId(courseId);
        courseRepo.update(courseDetails);
        return courseDetails;
    }

    @Override
    public Course partialUpdateCourse(Integer courseId, Course courseDetails) {
        Course existingCourse = courseRepo.findById(courseId);
        if (existingCourse == null) {
            // throw new ResourceNotFoundException("Course not found with id: " + courseId);
            return null;
        }

        boolean updated = false;
        if (courseDetails.getDepartmentId() != null) {
            existingCourse.setDepartmentId(courseDetails.getDepartmentId());
            updated = true;
        }
        if (courseDetails.getName() != null) {
            existingCourse.setName(courseDetails.getName());
            updated = true;
        }
        if (courseDetails.getCode() != null) {
            existingCourse.setCode(courseDetails.getCode());
            updated = true;
        }
        if (courseDetails.getCreditHours() != null) {
            existingCourse.setCreditHours(courseDetails.getCreditHours());
            updated = true;
        }

        if (updated) {
            courseRepo.update(existingCourse);
        }
        return existingCourse;
    }

    @Override
    public void deleteCourse(Integer courseId) {
        Course existingCourse = courseRepo.findById(courseId);
        if (existingCourse == null) {
            // throw new ResourceNotFoundException("Course not found with id: " + courseId);
            return;
        }
        // Consider referential integrity: what happens if classes reference this course?
        courseRepo.delete(courseId);
    }
}