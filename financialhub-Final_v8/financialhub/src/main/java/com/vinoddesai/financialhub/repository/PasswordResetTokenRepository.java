package com.maheshpai.financialhub.repository;

import com.maheshpai.financialhub.entity.Admin;
import com.maheshpai.financialhub.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByAdmin(Admin admin);
}