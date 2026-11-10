package com.maheshpai.financialhub.controller;

import com.maheshpai.financialhub.entity.Admin;
import com.maheshpai.financialhub.entity.PasswordResetToken;
import com.maheshpai.financialhub.repository.AdminRepository;
import com.maheshpai.financialhub.repository.PasswordResetTokenRepository;
import com.maheshpai.financialhub.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Endpoints used by /admin/forgot-password.html.
 * That page expects a session CSRF token, then JSON request/confirm calls.
 *
 * Changelog:
 *  - Email failures no longer roll back the token creation or leak a 500 to the
 *    browser. The generic success message is always returned (anti-enumeration),
 *    and the failure is logged for the operator.
 */
@RestController
@RequestMapping("/admin/api/password-reset")
public class PasswordResetController {

    static final String CSRF_SESSION_KEY = "PASSWORD_RESET_CSRF";
    static final String CSRF_HEADER = "X-CSRF-Token";

    private static final Logger log = LoggerFactory.getLogger(PasswordResetController.class);

    private final AdminRepository adminRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetController(AdminRepository adminRepository,
                                   PasswordResetTokenRepository tokenRepository,
                                   EmailService emailService,
                                   PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Issues a fresh single-use CSRF token for the session.
     * NOTE: tokens are single-use (see consumeCsrf) - the frontend must call
     * this again before the confirm step.
     */
    @GetMapping("/csrf")
    public Map<String, String> csrf(HttpServletRequest request) {
        String token = UUID.randomUUID().toString();
        request.getSession(true).setAttribute(CSRF_SESSION_KEY, token);
        return Map.of("token", token, "headerName", CSRF_HEADER);
    }

    @PostMapping("/request")
    @Transactional
    public ResponseEntity<Map<String, String>> requestReset(@RequestBody Map<String, String> body,
                                                            HttpServletRequest request) {
        if (!consumeCsrf(request)) {
            return ResponseEntity.status(403).body(Map.of("status", "forbidden"));
        }

        String email = body.get("email") == null ? "" : body.get("email").trim();
        if (!email.isEmpty()) {
            adminRepository.findByEmailIgnoreCase(email).ifPresent(this::issueResetToken);
        }

        // Always the same message - never reveal whether the account exists or
        // whether the email actually got sent.
        return ResponseEntity.ok(Map.of("status", "accepted"));
    }

    @PostMapping("/confirm")
    @Transactional
    public ResponseEntity<Map<String, String>> confirmReset(@RequestBody Map<String, String> body,
                                                            HttpServletRequest request) {
        if (!consumeCsrf(request)) {
            return ResponseEntity.status(403).body(Map.of("status", "forbidden"));
        }

        String token = body.get("token");
        String password = body.get("password");
        if (token == null || token.isBlank() || password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("status", "invalid"));
        }

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenOpt.ifPresent(tokenRepository::delete);
            return ResponseEntity.badRequest().body(Map.of("status", "invalid"));
        }

        PasswordResetToken resetToken = tokenOpt.get();
        Admin admin = resetToken.getAdmin();
        admin.setPassword(passwordEncoder.encode(password));
        adminRepository.save(admin);
        tokenRepository.delete(resetToken);

        return ResponseEntity.ok(Map.of("status", "reset"));
    }

    private void issueResetToken(Admin admin) {
        if (admin.getEmail() == null || admin.getEmail().isBlank()) {
            return;
        }
        tokenRepository.deleteByAdmin(admin);
        tokenRepository.flush();

        String token = UUID.randomUUID().toString();
        tokenRepository.save(new PasswordResetToken(token, admin, 15));

        try {
            emailService.sendPasswordResetEmail(admin.getEmail(), token);
        } catch (RuntimeException ex) {
            // FIX: do NOT rethrow. A rethrow rolls back the transaction (token
            // never persisted) and turns the response into a 500, which leaks
            // the failure to the caller. Log it, keep the generic response.
            log.error("Failed to send password reset email to {}", admin.getEmail(), ex);
        }
    }

    private boolean consumeCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(CSRF_SESSION_KEY);
        String actual = request.getHeader(CSRF_HEADER);
        session.removeAttribute(CSRF_SESSION_KEY);
        if (!(expected instanceof String expectedToken) || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expectedToken.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
