package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.config.security.JwtUtil;
import org.example.roomschedulerapi.classroomscheduler.model.Admin;
import org.example.roomschedulerapi.classroomscheduler.model.Department;
import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.example.roomschedulerapi.classroomscheduler.model.Role;
import org.example.roomschedulerapi.classroomscheduler.model.dto.*;
import org.example.roomschedulerapi.classroomscheduler.repository.AdminRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.DepartmentRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.InstructorRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.RoleRepository;
import org.example.roomschedulerapi.classroomscheduler.service.AuthService;
import org.example.roomschedulerapi.classroomscheduler.service.EmailService;
import org.example.roomschedulerapi.classroomscheduler.service.OtpService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final InstructorRepository instructorRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final OtpService otpService;
    private final AdminRepository adminRepository;

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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
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
        return authentication.getPrincipal();
    }

    @Override
    @Transactional
    public void processForgotPassword(String email, String frontendResetUrl) {
        // Find user by email. We use findByEmail from our CustomUserDetailsService logic before.
        // It's better to reuse that logic. For simplicity here, we query directly.
        // Using instructorRepository as an example. Your CustomUserDetailsService handles both.
        Optional<Instructor> userOptional = instructorRepository.findByEmail(email);

        // For security, don't reveal if the user exists.
        // If the user exists, send the email. If not, do nothing but act as if it succeeded.
        if (userOptional.isPresent()) {
            UserDetails userDetails = userOptional.get();
            String token = jwtUtil.generatePasswordResetToken(userDetails);

            // Example URL: http://localhost:3000/auth/reset-password?token=...
            String resetLink = frontendResetUrl + "?token=" + token;

            emailService.sendPasswordResetEmail(email, resetLink); // Create this new method in EmailService
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // 1. Validate the token and extract the username (email)
        String userEmail = jwtUtil.extractUsername(token);
        UserDetails userDetails = instructorRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NoSuchElementException("User not found from token."));

        if (!jwtUtil.isTokenValid(token, userDetails)) {
            throw new IllegalArgumentException("Password reset token is invalid or has expired.");
        }

        // 2. We have a valid token and user, now update the password
        Instructor instructor = (Instructor) userDetails; // Cast to your entity
        instructor.setPassword(passwordEncoder.encode(newPassword));
        instructorRepository.save(instructor);

        // TODO: Optionally, invalidate all other existing JWTs for this user.
    }

    @Override
    @Transactional
    public void resetPasswordWithOtp(ResetPasswordWithOtpRequestDto request) {
        // 1. Validate the OTP
        String serverOtp = otpService.getOtp(request.getEmail());
        if (serverOtp == null || !serverOtp.equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid or expired OTP.");
        }

        // 2. Find the user (checking both Admin and Instructor repositories) - CORRECTED LOGIC
        UserDetails userDetails;

        // First, try to find the user as an Admin
        Optional<Admin> adminOptional = adminRepository.findByEmail(request.getEmail());

        if (adminOptional.isPresent()) {
            userDetails = adminOptional.get();
        } else {
            // If not found as an Admin, then search for an Instructor
            userDetails = instructorRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new NoSuchElementException("User not found with email: " + request.getEmail()));
        }

        // At this point, userDetails is guaranteed to be either an Admin or an Instructor

        // 3. Hash the new password and update the user
        String hashedPassword = passwordEncoder.encode(request.getNewPassword());

        // We need to cast to the concrete type to use the setter
        if (userDetails instanceof Admin) {
            Admin admin = (Admin) userDetails;
            admin.setPassword(hashedPassword);
            adminRepository.save(admin);
        } else if (userDetails instanceof Instructor) {
            Instructor instructor = (Instructor) userDetails;
            instructor.setPassword(hashedPassword);
            instructorRepository.save(instructor);
        }

        // 4. Clear the OTP so it can't be used again
        otpService.clearOtp(request.getEmail());
    }

    @Override
    @Transactional
    public void changePassword(Authentication authentication, ChangePasswordRequest request) {
        // 1. Get the authenticated user's email
        String email = authentication.getName();

        // 2. Find the user (check both Admin and Instructor)
        UserDetails userDetails;

        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent()) {
            userDetails = adminOptional.get();
        } else {
            userDetails = instructorRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
        }

        // 3. Verify current password matches
        if (!passwordEncoder.matches(request.getCurrentPassword(), userDetails.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // 4. Encode and update the new password
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

        if (userDetails instanceof Admin) {
            Admin admin = (Admin) userDetails;
            admin.setPassword(encodedNewPassword);
            adminRepository.save(admin);
        } else if (userDetails instanceof Instructor) {
            Instructor instructor = (Instructor) userDetails;
            instructor.setPassword(encodedNewPassword);
            instructorRepository.save(instructor);
        }
    }
}