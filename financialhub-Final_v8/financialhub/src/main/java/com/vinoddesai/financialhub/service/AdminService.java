package com.maheshpai.financialhub.service;

import com.maheshpai.financialhub.entity.Admin;
import com.maheshpai.financialhub.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public boolean verifyAdminLogin(String username, String rawPassword) {
        // 1. Safely fetch the admin without SQL injection risk
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            
            // 2. Safely compare passwords (If using plaintext for now)
            // In a production environment, you should use BCrypt to hash passwords!
            if (admin.getPassword().equals(rawPassword)) {
                return true;
            }
        }
        return false;
    }
}