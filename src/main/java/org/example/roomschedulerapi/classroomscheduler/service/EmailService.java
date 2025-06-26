package org.example.roomschedulerapi.classroomscheduler.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendPasswordResetEmail(String toEmail, String resetLink);
}
