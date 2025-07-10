package org.example.roomschedulerapi.classroomscheduler.service.impl; // Adjust to your package

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.Department;
import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.example.roomschedulerapi.classroomscheduler.model.Role;
import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.InstructorUpdateDto;
import org.example.roomschedulerapi.classroomscheduler.repository.DepartmentRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.InstructorRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.RoleRepository;
import org.example.roomschedulerapi.classroomscheduler.service.InstructorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final DepartmentRepository departmentRepository; // To fetch Department by ID
    // RoleRepository and PasswordEncoder are no longer needed for patching but may be used elsewhere
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    // ... (convertToDto and other methods remain the same)
    private InstructorResponseDto convertToDto(Instructor instructor) {
        if (instructor == null) {
            return null;
        }
        return new InstructorResponseDto(
                instructor.getInstructorId(),
                instructor.getFirstName(),
                instructor.getLastName(),
                instructor.getEmail(),
                instructor.getPhone(),
                instructor.getDegree(),
                instructor.getMajor(),
                instructor.getProfile(),
                instructor.getAddress(),
                instructor.isArchived(),
                instructor.getRole() != null ? instructor.getRole().getRoleName() : null,
                instructor.getDepartment() != null ? instructor.getDepartment().getDepartmentId() : null,
                instructor.getDepartment() != null ? instructor.getDepartment().getName() : null
        );
    }
    @Override
    public List<InstructorResponseDto> getAllInstructors(Boolean isArchived) {
        List<Instructor> instructors;
        if (isArchived == null) {
            instructors = instructorRepository.findAll();
        } else {
            instructors = instructorRepository.findByIsArchived(isArchived);
        }
        return instructors.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<InstructorResponseDto> getInstructorById(Long instructorId) {
        return instructorRepository.findById(instructorId).map(this::convertToDto);
    }

    @Override
    @Transactional
    public InstructorResponseDto createInstructor(InstructorCreateDto dto) {
        // Check if email already exists
        instructorRepository.findByEmail(dto.getEmail()).ifPresent(existingInstructor -> {
            throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
        });

        Instructor instructor = new Instructor();
        instructor.setFirstName(dto.getFirstName());
        instructor.setLastName(dto.getLastName());
        instructor.setEmail(dto.getEmail());
        instructor.setPassword(passwordEncoder.encode(dto.getPassword())); // Hash password
        instructor.setPhone(dto.getPhone());
        instructor.setDegree(dto.getDegree());
        instructor.setMajor(dto.getMajor());
        instructor.setProfile(dto.getProfile());
        instructor.setAddress(dto.getAddress());
        instructor.setArchived(false); // Default for new instructors

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new NoSuchElementException("Role not found with id: " + dto.getRoleId()));
        instructor.setRole(role);

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("Department not found with id: " + dto.getDepartmentId()));
        instructor.setDepartment(department);

        Instructor savedInstructor = instructorRepository.save(instructor);
        return convertToDto(savedInstructor);
    }

    @Override
    @Transactional
    public InstructorResponseDto patchInstructor(Long instructorId, InstructorUpdateDto dto) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with id: " + instructorId));

        // Update fields only if they are provided in the DTO
        if (StringUtils.hasText(dto.getFirstName())) {
            instructor.setFirstName(dto.getFirstName());
        }
        if (StringUtils.hasText(dto.getLastName())) {
            instructor.setLastName(dto.getLastName());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            if (!instructor.getEmail().equals(dto.getEmail())) {
                instructorRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
                    if (!existing.getInstructorId().equals(instructorId)) {
                        throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
                    }
                });
                instructor.setEmail(dto.getEmail());
            }
        }
        if (StringUtils.hasText(dto.getPhone())) {
            instructor.setPhone(dto.getPhone());
        }
        if (StringUtils.hasText(dto.getDegree())) {
            instructor.setDegree(dto.getDegree());
        }
        if (StringUtils.hasText(dto.getMajor())) {
            instructor.setMajor(dto.getMajor());
        }
        if (StringUtils.hasText(dto.getAddress())) {
            instructor.setAddress(dto.getAddress());
        }

        // Corrected variable name from 'updateDto' to 'dto'
        if (StringUtils.hasText(dto.getProfile())) {
            instructor.setProfile(dto.getProfile());
        }

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new NoSuchElementException("Department not found with id: " + dto.getDepartmentId()));
            instructor.setDepartment(department);
        }

        Instructor updatedInstructor = instructorRepository.save(instructor);
        return convertToDto(updatedInstructor);
    }

    @Override
    @Transactional
    public InstructorResponseDto archiveInstructor(Long instructorId, boolean archiveStatus) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with id: " + instructorId));
        instructor.setArchived(archiveStatus);
        Instructor savedInstructor = instructorRepository.save(instructor);
        return convertToDto(savedInstructor);
    }

    @Override
    @Transactional
    public void deleteInstructor(Long instructorId) {
        if (!instructorRepository.existsById(instructorId)) {
            throw new NoSuchElementException("Instructor not found with id: " + instructorId);
        }
        instructorRepository.deleteById(instructorId);
    }

    @Override
    @Transactional
    public void resetPasswordByAdmin(Long instructorId, String newPassword) {
        // 1. Find the target instructor by their ID
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with id: " + instructorId));

        // 2. Encode the new password before saving
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 3. Set the new, encoded password
        instructor.setPassword(encodedPassword);

        // 4. Save the updated instructor to the database
        instructorRepository.save(instructor);
    }
}