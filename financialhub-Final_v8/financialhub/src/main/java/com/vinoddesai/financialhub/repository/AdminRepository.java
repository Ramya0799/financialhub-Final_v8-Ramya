package com.maheshpai.financialhub.repository;

import com.maheshpai.financialhub.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    // SAFE FROM SQL INJECTION: 
    // Spring Boot automatically converts this to a secure Prepared Statement.
    // It safely escapes any malicious characters the user types in.
    Optional<Admin> findByUsername(String username);

    Optional<Admin> findByEmailIgnoreCase(String email);
}