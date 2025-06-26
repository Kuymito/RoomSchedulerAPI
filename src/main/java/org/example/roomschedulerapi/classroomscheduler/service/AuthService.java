package org.example.roomschedulerapi.classroomscheduler.service;

import org.example.roomschedulerapi.classroomscheduler.model.dto.AuthRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.AuthResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.RegisterRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ResetPasswordWithOtpRequestDto;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);
    AuthResponseDto login(AuthRequestDto request);
    Object getCurrentUserProfile(Authentication authentication);
    void processForgotPassword(String email, String frontendResetUrl);
    void resetPassword(String token, String newPassword);
    void resetPasswordWithOtp(ResetPasswordWithOtpRequestDto request);
}