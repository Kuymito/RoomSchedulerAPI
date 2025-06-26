package org.example.roomschedulerapi.classroomscheduler.service; // Adjust to your package

import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorUpdateDto;

import java.util.List;
import java.util.Optional;

public interface InstructorService {

    List<InstructorResponseDto> getAllInstructors(Boolean isArchived);

    Optional<InstructorResponseDto> getInstructorById(Long instructorId);

    InstructorResponseDto createInstructor(InstructorCreateDto instructorCreateDto);

    InstructorResponseDto updateInstructor(Long instructorId, InstructorUpdateDto instructorUpdateDto);

    InstructorResponseDto archiveInstructor(Long instructorId, boolean archiveStatus);

    void deleteInstructor(Long instructorId); // Hard delete
}