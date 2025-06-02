// FILE: org/example/mybatisdemo/service/InstructorServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Instructor;
import org.example.roomschedulerapi.repository.InstructorRepository;
// import org.example.mybatisdemo.exception.ResourceNotFoundException; // Assuming you have this
import org.example.roomschedulerapi.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepo;

    public InstructorServiceImpl(InstructorRepository instructorRepo) {
        this.instructorRepo = instructorRepo;
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorRepo.findAll();
    }

    @Override
    public Instructor getInstructorById(Integer instructorId) {
        Instructor instructor = instructorRepo.findById(instructorId);
        // if (instructor == null) {
        //     throw new ResourceNotFoundException("Instructor not found with id: " + instructorId);
        // }
        return instructor;
    }

    @Override
    public Instructor getInstructorByUserId(Integer userId) {
        Instructor instructor = instructorRepo.findByUserId(userId);
        // if (instructor == null) {
        //     throw new ResourceNotFoundException("Instructor not found for user id: " + userId);
        // }
        return instructor;
    }

    @Override
    public Instructor createInstructor(Instructor instructor) {
        // Add validation if needed (e.g., check if user_id exists and is valid)
        instructorRepo.insert(instructor);
        return instructor;
    }

    @Override
    public Instructor updateInstructor(Integer instructorId, Instructor instructorDetails) {
        Instructor existingInstructor = instructorRepo.findById(instructorId);
        if (existingInstructor == null) {
            // throw new ResourceNotFoundException("Instructor not found with id: " + instructorId);
            return null; // Or throw exception
        }
        instructorDetails.setInstructorId(instructorId); // Ensure ID is set for update
        instructorRepo.update(instructorDetails);
        return instructorDetails;
    }

    @Override
    public Instructor partialUpdateInstructor(Integer instructorId, Instructor instructorDetails) {
        Instructor existingInstructor = instructorRepo.findById(instructorId);
        if (existingInstructor == null) {
            // throw new ResourceNotFoundException("Instructor not found with id: " + instructorId);
            return null; // Or throw exception
        }

        boolean updated = false;
        if (instructorDetails.getUserId() != null) {
            existingInstructor.setUserId(instructorDetails.getUserId());
            updated = true;
        }
        if (instructorDetails.getName() != null) {
            existingInstructor.setName(instructorDetails.getName());
            updated = true;
        }
        if (instructorDetails.getPhone() != null) {
            existingInstructor.setPhone(instructorDetails.getPhone());
            updated = true;
        }
        if (instructorDetails.getDegree() != null) {
            existingInstructor.setDegree(instructorDetails.getDegree());
            updated = true;
        }
        if (instructorDetails.getAddress() != null) {
            existingInstructor.setAddress(instructorDetails.getAddress());
            updated = true;
        }

        if (updated) {
            instructorRepo.update(existingInstructor);
        }
        return existingInstructor;
    }

    @Override
    public void deleteInstructor(Integer instructorId) {
        Instructor existingInstructor = instructorRepo.findById(instructorId);
        if (existingInstructor == null) {
            // throw new ResourceNotFoundException("Instructor not found with id: " + instructorId);
            // Or simply do nothing if delete is idempotent
            return;
        }
        instructorRepo.delete(instructorId);
    }
}