package com.maheshpai.financialhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ---> THIS IS THE BEAN SPRING BOOT WAS LOOKING FOR <---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // 1. All website files and uploaded images are fully public
                .requestMatchers("/", "/index.html", "/pages/**", "/components/**", "/assets/**", "/uploads/**").permitAll()
                
                // 2. Public Auth and Contact APIs
                .requestMatchers("/api/contact/**", "/api/auth/**").permitAll()
                
                // 3. Allow public website to READ the database for gallery, events, and blogs
                .requestMatchers(HttpMethod.GET, "/admin/api/blogs", "/admin/api/events", "/admin/api/gallery").permitAll()
                
                // 4. Allow access to login and password reset pages
                .requestMatchers("/admin/login.html", "/admin/forgot-password.html", "/admin/reset-password.html").permitAll()

                // Forgot-password page calls these before the user is signed in
                .requestMatchers("/admin/api/password-reset/**").permitAll()
                
                // 5. SECURE the admin dashboard and all modification APIs (POST, DELETE)
                .requestMatchers("/admin/dashboard.html", "/admin/api/**").authenticated()
                
                // 6. Catch all
                .anyRequest().permitAll() 
            )
           .formLogin(form -> form
        .loginPage("/admin/login.html")
        .loginProcessingUrl("/admin/login-submit")
        .defaultSuccessUrl("/admin/dashboard.html", true)
        .permitAll()
)
.oauth2Login(oauth2 -> oauth2
        .defaultSuccessUrl("/admin/dashboard.html", true)  // where Google users land after login
)
.logout(logout -> logout
        .logoutUrl("/admin/logout")
        .logoutSuccessUrl("/index.html")
        .permitAll()
);
           

        return http.build();
    }
}
