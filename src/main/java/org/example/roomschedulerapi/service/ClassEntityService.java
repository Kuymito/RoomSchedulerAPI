// FILE: org/example/roomschedulerapi/service/ClassEntityService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.ClassEntity; // Assuming this is your model
import java.util.List;

public interface ClassEntityService {
    List<ClassEntity> getAllClassEntities();
    ClassEntity getClassEntityById(Integer classId);
    List<ClassEntity> getClassEntitiesByInstructorId(Integer instructorId);
    List<ClassEntity> getClassEntitiesByCourseId(Integer courseId);
    ClassEntity createClassEntity(ClassEntity classEntity);
    ClassEntity updateClassEntity(Integer classId, ClassEntity classDetails);
    ClassEntity partialUpdateClassEntity(Integer classId, ClassEntity classDetails);
    void archiveClassEntity(Integer classId);
    void deleteClassEntity(Integer classId);
}