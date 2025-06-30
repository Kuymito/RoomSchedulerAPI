package org.example.roomschedulerapi.classroomscheduler.service;

import org.example.roomschedulerapi.classroomscheduler.model.dto.*;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);
    AuthResponseDto login(AuthRequestDto request);
    Object getCurrentUserProfile(Authentication authentication);
    void processForgotPassword(String email, String frontendResetUrl);
    void resetPassword(String token, String newPassword);
    void resetPasswordWithOtp(ResetPasswordWithOtpRequestDto request);
    void changePassword(Authentication authentication, ChangePasswordRequest request);
}