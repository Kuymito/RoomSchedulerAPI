package org.example.roomschedulerapi.classroomscheduler.service.impl;

import org.example.roomschedulerapi.classroomscheduler.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your OTP Code for Verification");
            message.setText("Hello,\n\nYour One-Time Password (OTP) for verification is: " + otp +
                    "\n\nThis code is valid for 5 minutes. Please do not share it with anyone." +
                    "\n\nThank you,\nThe Application Team");

            javaMailSender.send(message);
        } catch (Exception e) {
            // Handle mail sending exceptions, e.g., log them
            // In a real application, you might want to throw a custom exception
            System.err.println("Error sending OTP email: " + e.getMessage());
            // For now, we'll let it fail silently in the background, but production code should handle this better.
        }
    }

    @Async // Runs this task in a background thread
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
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
            System.err.println("Error sending password reset email: " + e.getMessage());
        }
    }
}