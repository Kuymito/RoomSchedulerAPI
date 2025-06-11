package org.example.roomschedulerapi.classroomscheduler.service.impl;

import org.example.roomschedulerapi.classroomscheduler.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
}