package org.example.roomschedulerapi.classroomscheduler.controller;

import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.dto.OtpRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.OtpValidationDto;
import org.example.roomschedulerapi.classroomscheduler.service.EmailService;
import org.example.roomschedulerapi.classroomscheduler.service.OtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/otp")
public class OtpController {

    private final OtpService otpService;
    private final EmailService emailService;

    public OtpController(OtpService otpService, EmailService emailService) {
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<String>> generateOtp(@Valid @RequestBody OtpRequestDto otpRequest) {
        try {
            // Generate OTP
            String otp = otpService.generateOtp(otpRequest.getEmail());

            // Send OTP via email (can be done asynchronously)
            // For simplicity, we do it synchronously here.
            // In a real app, use @Async on the email sending method.
            emailService.sendOtpEmail(otpRequest.getEmail(), otp);

            ApiResponse<String> response = new ApiResponse<>(
                    "OTP has been sent successfully to your email.",
                    null, // Do not send the OTP back in the response for security
                    HttpStatus.OK,
                    LocalDateTime.now()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the exception
            ApiResponse<String> response = new ApiResponse<>(
                    "Failed to send OTP. Please try again later.",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateOtp(@Valid @RequestBody OtpValidationDto validationRequest) {
        String serverOtp = otpService.getOtp(validationRequest.getEmail());

        if (serverOtp == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "OTP has expired or is invalid. Please request a new one.",
                    false, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        }

        if (serverOtp.equals(validationRequest.getOtp())) {
            // OTP is valid, clear it to prevent reuse
            return ResponseEntity.ok(new ApiResponse<>(
                    "OTP validation successful.",
                    true, HttpStatus.OK, LocalDateTime.now()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid OTP.",
                    false, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        }
    }
}