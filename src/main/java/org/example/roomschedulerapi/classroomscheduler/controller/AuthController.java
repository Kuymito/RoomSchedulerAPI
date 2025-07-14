package org.example.roomschedulerapi.classroomscheduler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.config.security.JwtUtil;
import org.example.roomschedulerapi.classroomscheduler.model.Admin;
import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.example.roomschedulerapi.classroomscheduler.model.dto.*;
import org.example.roomschedulerapi.classroomscheduler.repository.AdminRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.InstructorRepository;
import org.example.roomschedulerapi.classroomscheduler.service.AuthService;
import org.example.roomschedulerapi.classroomscheduler.service.InstructorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final InstructorRepository instructorRepository;
    private final InstructorService instructorService;
    private final AdminRepository adminRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDto>> registerUser(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {
            AuthResponseDto responseDto = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                    "User registered successfully. Please use the token to log in.",
                    responseDto,
                    HttpStatus.CREATED,
                    LocalDateTime.now()
            ));
        } catch (IllegalArgumentException | NoSuchElementException e) {
            // Handle specific, known errors (like email exists, role not found)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login")
    public AuthResponseDto createAuthenticationToken(@RequestBody AuthRequestDto authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return new AuthResponseDto(jwt);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Optional<Instructor> instructorOptional = instructorRepository.findByEmail(email);

        if (instructorOptional.isPresent()) {
            // Case 1: The user is an instructor.
            Instructor instructor = instructorOptional.get();

            // Get the department name, handling the possibility of a null department
            String departmentName = (instructor.getDepartment() != null) ? instructor.getDepartment().getName() : null;

            UserProfileResponse userProfile = new UserProfileResponse(
                    instructor.getFirstName(),
                    instructor.getLastName(),
                    email,
                    roles,
                    instructor.getProfile(),
                    departmentName ,
                    instructor.getMajor(),
                    instructor.getPhone(),
                    instructor.getDegree(),
                    instructor.getInstructorId(),
                    instructor.getAddress()
            );
            return ResponseEntity.ok(userProfile);
        } else if (roles.contains("ROLE_ADMIN")) {

            // Case 2: The user is an admin but not in the instructor table.
            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                UserProfileResponse adminProfile = new UserProfileResponse(
                        admin.getFirstName(),
                        admin.getLastName(),
                        email,
                        roles,
                        admin.getProfile(),
                        null, // No department for admin
                        null, // No major for admin
                        admin.getPhoneNumber(),
                        null, // No degree for admin
                        (long) admin.getAdminId(),
                        admin.getAddress()
                );
                return ResponseEntity.ok(adminProfile);
            } else {
                // This case should ideally not happen if the JWT is valid and the user is in the database.
                throw new NoSuchElementException("Admin profile not found for email: " + email);
            }
        } else {
            // Case 3: User not found.
            throw new java.util.NoSuchElementException("User profile not found for email: " + email);
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        // Always return a generic success message to prevent user enumeration attacks
        // The frontendResetUrl should be configured in your application properties or sent by the client
        String frontendUrl = "http://localhost:3000/auth/reset-password";
        authService.processForgotPassword(request.getEmail(), frontendUrl);

        return ResponseEntity.ok(new ApiResponse<>(
                "If an account with that email exists, a password reset link has been sent.",
                null, HttpStatus.OK, LocalDateTime.now()));
    }

    @PostMapping("/reset-password-with-otp")
    public ResponseEntity<ApiResponse<String>> resetPasswordWithOtp(@Valid @RequestBody ResetPasswordWithOtpRequestDto request) {
        try {
            authService.resetPasswordWithOtp(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Password has been reset successfully.",
                    null, HttpStatus.OK, LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Error resetting password: " + e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        try {
            authService.changePassword(authentication, request);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Password changed successfully",
                    null,
                    HttpStatus.OK,
                    LocalDateTime.now()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    e.getMessage(),
                    null,
                    HttpStatus.BAD_REQUEST,
                    LocalDateTime.now()
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    "User not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    LocalDateTime.now()
            ));
        }
    }

    @PatchMapping("/{instructorId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')") // This ensures only admins can access it
    public ResponseEntity<Void> resetInstructorPasswordByAdmin(
            @PathVariable Long instructorId,
            @RequestBody AdminPasswordResetDto passwordResetDto) {

        instructorService.resetPasswordByAdmin(instructorId, passwordResetDto.getNewPassword());

        return ResponseEntity.ok().build();
    }
}

// Create these DTOs: AuthRequestDto.java and AuthResponseDto.java
// @Data public class AuthRequestDto { private String email; private String password; }
// @Data @AllArgsConstructor public class AuthResponseDto { private String token; }