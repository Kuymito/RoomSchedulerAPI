package org.example.roomschedulerapi.classroomscheduler.service.impl;

import org.example.roomschedulerapi.classroomscheduler.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            System.out.println("Attempting to send OTP email synchronously...");
            SimpleMailMessage message = new SimpleMailMessage();

            // Set the 'From' address to match the authenticated user
            message.setFrom(fromEmail);

            message.setTo(toEmail);
            message.setSubject("Your OTP Code for Verification");
            message.setText("Hello,\n\nYour One-Time Password (OTP) for verification is: " + otp +
                    "\n\nThis code is valid for 5 minutes. Please do not share it with anyone." +
                    "\n\nThank you,\nThe Application Team");

            javaMailSender.send(message);
            System.out.println("Email sending process completed.");

        } catch (Exception e) {
            System.err.println("Error caught in EmailServiceImpl. Re-throwing...");
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail); // Also add the fix here for consistency
            message.setTo(toEmail);
            message.setSubject("Password Reset Request");
            message.setText(
                    "Hello,\n\n" +
                            "A request has been made to reset the password for your account.\n\n" +
                            "Please click the link below to reset your password:\n" +
                            resetLink + "\n\n" +
                            "This link is valid for 15 minutes.\n\n" +
                            "If you did not request a password reset, please ignore this email."
            );
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage(), e);
        }
    }
}