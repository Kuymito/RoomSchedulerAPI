package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.config.security.JwtUtil;
import org.example.roomschedulerapi.classroomscheduler.model.Department;
import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.example.roomschedulerapi.classroomscheduler.model.Role;
import org.example.roomschedulerapi.classroomscheduler.model.dto.AuthRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.AuthResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.RegisterRequestDto;
import org.example.roomschedulerapi.classroomscheduler.repository.DepartmentRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.InstructorRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.RoleRepository;
import org.example.roomschedulerapi.classroomscheduler.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final InstructorRepository instructorRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        // 1. Check if user already exists
        if (instructorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email address is already in use.");
        }

        // 2. Find Department and Role by ID
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("Department not found with ID: " + request.getDepartmentId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new NoSuchElementException("Role not found with ID: " + request.getRoleId()));

        // 3. Create and save the new Instructor (User)
        Instructor instructor = new Instructor();
        instructor.setFirstName(request.getFirstName());
        instructor.setLastName(request.getLastName());
        instructor.setEmail(request.getEmail());
        instructor.setPassword(passwordEncoder.encode(request.getPassword())); // Hash the password
        instructor.setPhone(request.getPhone());
        instructor.setDegree(request.getDegree());
        instructor.setMajor(request.getMajor());
        instructor.setAddress(request.getAddress());
        instructor.setDepartment(department);
        instructor.setRole(role);
        instructor.setArchived(false); // New users are active by default

        Instructor savedInstructor = instructorRepository.save(instructor);

        // 4. Generate JWT token
        // The savedInstructor object implements UserDetails
        String jwtToken = jwtUtil.generateToken(savedInstructor);

        return new AuthResponseDto(jwtToken);
    }

    @Override
    public AuthResponseDto login(AuthRequestDto request) {
        // Authenticate the user. If credentials are bad, it will throw an exception.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // If authentication is successful, find the user and generate a token
        UserDetails userDetails = instructorRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("User not found after authentication."));

        String jwtToken = jwtUtil.generateToken(userDetails);
        return new AuthResponseDto(jwtToken);
    }

    @Override
    public Object getCurrentUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        // The principal is the UserDetails object we set in JwtAuthFilter
        return authentication.getPrincipal();
    }
}