package com.maheshpai.financialhub.config;

import com.maheshpai.financialhub.entity.Admin;
import com.maheshpai.financialhub.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseSeeder {

    @Bean
    public CommandLineRunner initDatabase(AdminRepository adminRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            // Only create the admin if the table is currently empty
            if (adminRepo.count() == 0) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Securely hashed
                
                // NEW: Added the client's official email for password resets
                admin.setEmail("vijaysutar0000@gmail.com");
                admin.setRole("ROLE_ADMIN");
                
                adminRepo.save(admin);
                System.out.println("Default Admin Created - Username: admin | Password: admin123 | Email: vijaysutar0000@gmail.com");
            } else {
                // Existing admins created before email was required cannot receive reset mail.
                boolean notificationEmailTaken = adminRepo.findAll().stream()
                        .anyMatch(existing -> "vijaysutar0000@gmail.com".equalsIgnoreCase(existing.getEmail()));
                for (Admin admin : adminRepo.findAll()) {
                    boolean changed = false;
                    if ((admin.getEmail() == null || admin.getEmail().isBlank()) && !notificationEmailTaken) {
                        admin.setEmail("testfinacial123@gmail.com");
                        notificationEmailTaken = true;
                        changed = true;
                    }
                    if (admin.getRole() == null || admin.getRole().isBlank()) {
                        admin.setRole("ROLE_ADMIN");
                        changed = true;
                    }
                    if (changed) {
                        adminRepo.save(admin);
                    }
                }
            }
        };
    }
}
