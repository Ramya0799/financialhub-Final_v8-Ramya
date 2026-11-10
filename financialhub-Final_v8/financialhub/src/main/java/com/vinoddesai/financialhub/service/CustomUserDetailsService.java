package com.maheshpai.financialhub.service;

import com.maheshpai.financialhub.entity.Admin;
import com.maheshpai.financialhub.repository.AdminRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private AdminRepository adminRepository;

    public CustomUserDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));

        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .roles(admin.getRole().replace("ROLE_", "")) // Spring adds "ROLE_" automatically
                .build();
    }

	public AdminRepository getAdminRepository() {
		return adminRepository;
	}

	public void setAdminRepository(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}
}