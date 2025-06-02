// FILE: org/example/mybatisdemo/service/InstructorService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Instructor;
import java.util.List;

public interface InstructorService {
    List<Instructor> getAllInstructors();
    Instructor getInstructorById(Integer instructorId);
    Instructor getInstructorByUserId(Integer userId);
    Instructor createInstructor(Instructor instructor);
    Instructor updateInstructor(Integer instructorId, Instructor instructorDetails);
    Instructor partialUpdateInstructor(Integer instructorId, Instructor instructorDetails);
    void deleteInstructor(Integer instructorId);
}