package com.maheshpai.financialhub.controller;

import com.maheshpai.financialhub.entity.Admin;
import com.maheshpai.financialhub.entity.PasswordResetToken;
import com.maheshpai.financialhub.repository.AdminRepository;
import com.maheshpai.financialhub.repository.PasswordResetTokenRepository;
import com.maheshpai.financialhub.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<?> processForgotPassword(@RequestParam("username") String username) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            tokenRepository.deleteByAdmin(admin);
            
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, admin, 15);
            tokenRepository.save(resetToken);
            
            emailService.sendPasswordResetEmail(admin.getEmail(), token);
        }
        return ResponseEntity.ok(Map.of("message", "If the account exists, a reset link has been sent."));
    }

    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(
            @RequestParam("token") String token, 
            @RequestParam("newPassword") String newPassword) {
        
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid reset token.");
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            return ResponseEntity.badRequest().body("Token has expired. Please request a new one.");
        }
        
        Admin admin = resetToken.getAdmin();
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
        tokenRepository.delete(resetToken);
        
        return ResponseEntity.ok("Password updated successfully.");
    }
}