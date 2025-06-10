package org.example.roomschedulerapi.classroomscheduler.service.impl; // Adjust to your package

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
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder; // For password hashing
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final RoleRepository roleRepository; // To fetch Role by ID
    private final DepartmentRepository departmentRepository; // To fetch Department by ID
    // private final PasswordEncoder passwordEncoder; // Inject if using Spring Security for hashing

    @Autowired
    public InstructorServiceImpl(InstructorRepository instructorRepository,
                                 RoleRepository roleRepository,
                                 DepartmentRepository departmentRepository
            /*, PasswordEncoder passwordEncoder */) {
        this.instructorRepository = instructorRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        // this.passwordEncoder = passwordEncoder;
    }

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
                instructor.getRole() != null ? instructor.getRole().getRoleName() : null, // Assuming Role has getRoleName()
                instructor.getDepartment() != null ? instructor.getDepartment().getName() : null // Assuming Department has getName()
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
        // instructor.setPassword(passwordEncoder.encode(dto.getPassword())); // Hash password
        instructor.setPassword(dto.getPassword()); // TODO: HASH PASSWORD PROPERLY
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
    public InstructorResponseDto updateInstructor(Long instructorId, InstructorUpdateDto dto) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with id: " + instructorId));

        if (dto.getFirstName() != null) {
            instructor.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            instructor.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            // Check if the new email is different and if it's already taken by another user
            if (!instructor.getEmail().equals(dto.getEmail())) {
                instructorRepository.findByEmail(dto.getEmail()).ifPresent(existingInstructor -> {
                    if (!existingInstructor.getInstructorId().equals(instructorId)) {
                        throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
                    }
                });
            }
            instructor.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            // instructor.setPassword(passwordEncoder.encode(dto.getPassword())); // Hash new password
            instructor.setPassword(dto.getPassword()); // TODO: HASH PASSWORD PROPERLY
        }
        if (dto.getPhone() != null) {
            instructor.setPhone(dto.getPhone());
        }
        if (dto.getDegree() != null) {
            instructor.setDegree(dto.getDegree());
        }
        if (dto.getMajor() != null) {
            instructor.setMajor(dto.getMajor());
        }
        if (dto.getProfile() != null) {
            instructor.setProfile(dto.getProfile());
        }
        if (dto.getAddress() != null) {
            instructor.setAddress(dto.getAddress());
        }
        if (dto.getRoleId() != null) {
            Role role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new NoSuchElementException("Role not found with id: " + dto.getRoleId()));
            instructor.setRole(role);
        }
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new NoSuchElementException("Department not found with id: " + dto.getDepartmentId()));
            instructor.setDepartment(department);
        }
        if (dto.getIsArchived() != null) { // For explicit archive via update DTO
            instructor.setArchived(dto.getIsArchived());
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
}