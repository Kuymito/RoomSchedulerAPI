package org.example.roomschedulerapi.classroomscheduler.service;

import org.example.roomschedulerapi.classroomscheduler.model.dto.AuthRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.AuthResponseDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.RegisterRequestDto;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);
    AuthResponseDto login(AuthRequestDto request);
    Object getCurrentUserProfile(Authentication authentication);
}