package org.example.roomschedulerapi.classroomscheduler.service;

public interface OtpService {
    String generateOtp(String key);
    String getOtp(String key);
    void clearOtp(String key);
}
