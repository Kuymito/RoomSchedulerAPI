// FILE: org/example/roomschedulerapi/service/ClassEntityServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.ClassEntity; // Assuming this is your model
import org.example.roomschedulerapi.repository.ClassEntityRepository; // Assuming this is your repository
// import org.example.roomschedulerapi.exception.ResourceNotFoundException; // If you have a custom exception
// Other repository imports for FK validation if needed
// import org.example.roomschedulerapi.repository.InstructorRepository;
// import org.example.roomschedulerapi.repository.CourseRepository;
// import org.example.roomschedulerapi.repository.RoomRepository;
// import org.example.roomschedulerapi.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassEntityServiceImpl implements ClassEntityService {

    private final ClassEntityRepository classEntityRepository;
    // Inject other repos if FK validation is needed:
    // private final InstructorRepository instructorRepository;
    // private final CourseRepository courseRepository;
    // private final RoomRepository roomRepository;
    // private final TimeSlotRepository timeSlotRepository;


    // Constructor should match the class name
    public ClassEntityServiceImpl(ClassEntityRepository classEntityRepository /*, other repos */) {
        this.classEntityRepository = classEntityRepository;
        // this.instructorRepository = instructorRepository; ...
    }

    @Override
    public List<ClassEntity> getAllClassEntities() {
        return classEntityRepository.findAll();
    }

    @Override
    public ClassEntity getClassEntityById(Integer classId) {
        // Assuming findById in repository returns ClassEntity or throws if not found.
        // Or it returns an Optional<ClassEntity>
        return classEntityRepository.findById(classId)
                .orElse(null); // Or .orElseThrow(() -> new ResourceNotFoundException("ClassEntity not found with id: " + classId));
    }

    @Override
    public List<ClassEntity> getClassEntitiesByInstructorId(Integer instructorId) {
        return classEntityRepository.findByInstructorId(instructorId);
    }

    @Override
    public List<ClassEntity> getClassEntitiesByCourseId(Integer courseId) {
        return classEntityRepository.findByCourseId(courseId);
    }

    @Override
    public ClassEntity createClassEntity(ClassEntity classEntity) {
        // Validate FKs: instructorId, courseId, roomId, timeslotId exist (using injected repos)
        // Validate: startDate < endDate
        // Validate: No scheduling conflicts (same instructor/room/timeslot at overlapping dates) - Complex!
        if (classEntity.getStartDate() != null && classEntity.getEndDate() != null &&
                classEntity.getStartDate().after(classEntity.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
        if (classEntity.getIsArchived() == null) {
            classEntity.setIsArchived(false); // Default if not provided
        }
        return classEntityRepository.save(classEntity); // Assuming Spring Data JPA 'save' or similar for MyBatis 'insert'
    }

    @Override
    public ClassEntity updateClassEntity(Integer classId, ClassEntity classDetails) {
        ClassEntity existingClass = classEntityRepository.findById(classId)
                .orElse(null); // Or throw ResourceNotFoundException

        if (existingClass == null) {
            // throw new ResourceNotFoundException("ClassEntity not found with id: " + classId);
            return null; // Or handle as appropriate
        }

        if (classDetails.getStartDate() != null && classDetails.getEndDate() != null &&
                classDetails.getStartDate().after(classDetails.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }

        // Update fields from classDetails to existingClass
        existingClass.setInstructorId(classDetails.getInstructorId());
        existingClass.setCourseId(classDetails.getCourseId());
        existingClass.setRoomId(classDetails.getRoomId());
        existingClass.setTimeslotId(classDetails.getTimeslotId());
        existingClass.setMaxStudents(classDetails.getMaxStudents());
        existingClass.setStartDate(classDetails.getStartDate());
        existingClass.setEndDate(classDetails.getEndDate());
        existingClass.setStatus(classDetails.getStatus());
        existingClass.setIsArchived(classDetails.getIsArchived() != null ? classDetails.getIsArchived() : existingClass.getIsArchived());


        return classEntityRepository.save(existingClass); // Or a custom update method in repository
    }

    @Override
    public ClassEntity partialUpdateClassEntity(Integer classId, ClassEntity classDetails) {
        ClassEntity existingClass = classEntityRepository.findById(classId)
                .orElse(null); // Or throw ResourceNotFoundException

        if (existingClass == null) {
            // throw new ResourceNotFoundException("ClassEntity not found with id: " + classId);
            return null;
        }

        boolean updated = false;
        if (classDetails.getInstructorId() != null) {
            existingClass.setInstructorId(classDetails.getInstructorId());
            updated = true;
        }
        if (classDetails.getCourseId() != null) {
            existingClass.setCourseId(classDetails.getCourseId());
            updated = true;
        }
        if (classDetails.getRoomId() != null) {
            existingClass.setRoomId(classDetails.getRoomId());
            updated = true;
        }
        if (classDetails.getTimeslotId() != null) {
            existingClass.setTimeslotId(classDetails.getTimeslotId());
            updated = true;
        }
        if (classDetails.getMaxStudents() != null) {
            existingClass.setMaxStudents(classDetails.getMaxStudents());
            updated = true;
        }
        if (classDetails.getStartDate() != null) {
            existingClass.setStartDate(classDetails.getStartDate());
            updated = true;
        }
        if (classDetails.getEndDate() != null) {
            existingClass.setEndDate(classDetails.getEndDate());
            updated = true;
        }
        if (classDetails.getStatus() != null) {
            existingClass.setStatus(classDetails.getStatus());
            updated = true;
        }
        if (classDetails.getIsArchived() != null) {
            existingClass.setIsArchived(classDetails.getIsArchived());
            updated = true;
        }

        if (existingClass.getStartDate() != null && existingClass.getEndDate() != null &&
                existingClass.getStartDate().after(existingClass.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date after partial update.");
        }

        if (updated) {
            return classEntityRepository.save(existingClass); // Or a custom update method
        }
        return existingClass; // Return existing if no updates were made
    }

    @Override
    public void archiveClassEntity(Integer classId) {
        ClassEntity existingClass = classEntityRepository.findById(classId)
                .orElse(null);

        if (existingClass == null) {
            // throw new ResourceNotFoundException("ClassEntity not found with id: " + classId);
            return;
        }
        // Assuming your repository has an 'archive' method or you update and save
        existingClass.setIsArchived(true);
        existingClass.setStatus("Archived");
        classEntityRepository.save(existingClass);
        // Or directly: classEntityRepository.archive(classId);
    }

    @Override
    public void deleteClassEntity(Integer classId) {
        ClassEntity existingClass = classEntityRepository.findById(classId)
                .orElse(null);

        if (existingClass == null) {
            // throw new ResourceNotFoundException("ClassEntity not found with id: " + classId);
            return;
        }
        // Check for related entities like requests before deletion
        classEntityRepository.deleteById(classId); // Assuming Spring Data JPA 'deleteById' or similar for MyBatis 'delete'
    }
}