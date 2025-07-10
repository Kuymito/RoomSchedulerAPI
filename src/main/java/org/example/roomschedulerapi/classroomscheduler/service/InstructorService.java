package org.example.roomschedulerapi.classroomscheduler.service; // Adjust to your package

import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorUpdateDto;

import java.util.List;
import java.util.Optional;

public interface InstructorService {
    List<InstructorResponseDto> getAllInstructors(Boolean isArchived);
    Optional<InstructorResponseDto> getInstructorById(Long instructorId);
    InstructorResponseDto createInstructor(InstructorCreateDto createDto);
    InstructorResponseDto patchInstructor(Long instructorId, InstructorUpdateDto updateDto); // Changed method name
    InstructorResponseDto archiveInstructor(Long instructorId, boolean archiveStatus);
    void deleteInstructor(Long instructorId);
    void resetPasswordByAdmin(Long instructorId, String newPassword);
}