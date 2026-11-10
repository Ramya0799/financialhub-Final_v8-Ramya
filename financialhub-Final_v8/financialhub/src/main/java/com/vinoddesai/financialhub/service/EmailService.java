package com.maheshpai.financialhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request - Vinod Desai's Financial Hub");
        
        String resetLink = "http://localhost:8080/admin/reset-password.html?token=" + resetToken;
        
        message.setText("Hello,\n\n" +
                "You requested a password reset for your admin account.\n" +
                "Click the link below to set a new password:\n\n" +
                resetLink + "\n\n" +
                "If you did not request this, please ignore this email.");
        
        mailSender.send(message);
    }
}
