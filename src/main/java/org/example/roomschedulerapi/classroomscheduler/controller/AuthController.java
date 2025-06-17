package org.example.roomschedulerapi.classroomscheduler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.config.security.JwtUtil;
import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.dto.*;
import org.example.roomschedulerapi.classroomscheduler.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

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
    public ResponseEntity<ApiResponse<Object>> getUserProfile(Authentication authentication) {
        // The JwtAuthFilter ensures that the 'authentication' object is populated
        // if a valid token is provided in the request header.
        Object userProfile = authService.getCurrentUserProfile(authentication);
        if (userProfile != null) {
            return ResponseEntity.ok(new ApiResponse<>(
                    "Profile retrieved successfully.",
                    userProfile,
                    HttpStatus.OK,
                    LocalDateTime.now()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    "Unauthorized: No valid token provided.", null, HttpStatus.UNAUTHORIZED, LocalDateTime.now()));
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

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse<>(
                    "Password has been reset successfully.",
                    null, HttpStatus.OK, LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Error resetting password: " + e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        }
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
}

// Create these DTOs: AuthRequestDto.java and AuthResponseDto.java
// @Data public class AuthRequestDto { private String email; private String password; }
// @Data @AllArgsConstructor public class AuthResponseDto { private String token; }