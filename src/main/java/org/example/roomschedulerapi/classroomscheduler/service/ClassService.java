package org.example.roomschedulerapi.classroomscheduler.service; // Adjust package

// Removed: import org.example.roomschedulerapi.classroomscheduler.model.Class;

import org.example.roomschedulerapi.classroomscheduler.model.dto.ClassCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ClassResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ClassUpdateDto;

import java.util.List;
import java.util.Optional;

public interface ClassService {

    List<ClassResponseDto> getAllClasses(Boolean isArchived); // Changed return type

    Optional<ClassResponseDto> getClassById(Long classId); // Changed return type

    ClassResponseDto createClass(ClassCreateDto dto); // Changed return type

    ClassResponseDto updateClass(Long classId, ClassCreateDto dto); // Changed return type

    ClassResponseDto patchClass(Long classId, ClassUpdateDto dto); // Changed return type

    ClassResponseDto archiveClass(Long classId, boolean archiveStatus); // Changed return type

    void deleteClass(Long classId);
}