package com.qurbaniconnect.config;

import com.qurbaniconnect.entity.Role;
import com.qurbaniconnect.entity.User;
import com.qurbaniconnect.enums.RoleName;
import com.qurbaniconnect.repository.RoleRepository;
import com.qurbaniconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Creates the first admin from ADMIN_EMAIL / ADMIN_PASSWORD environment variables. Never exposed over HTTP. */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements ApplicationRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin-seed.enabled}") private boolean enabled;
    @Value("${app.admin-seed.email}") private String email;
    @Value("${app.admin-seed.password}") private String password;
    @Value("${app.admin-seed.phone}") private String phone;
    @Value("${app.admin-seed.full-name}") private String fullName;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("[Config] AdminSeeder starting");
        
        if (!enabled) {
            log.info("[Config] Admin seeding is disabled, skipping");
            return;
        }
        log.info("[Config] Admin seeding is enabled");
        
        if (userRepository.existsByRoles_RoleName(RoleName.ROLE_ADMIN)) {
            log.info("[Config] Admin user already exists, skipping seed");
            return;
        }
        log.info("[Config] No admin user found, proceeding with seed");
        
        if (email == null || email.isBlank() || password == null || password.length() < 8) {
            log.warn("[Config] No admin exists and ADMIN_EMAIL / ADMIN_PASSWORD (min 8 chars) are not set; admin was NOT created.");
            return;
        }
        log.info("[Config] Admin credentials validated, email={}, phone={}, fullName={}", email, phone, fullName);
        
        log.info("[Config] Looking up ROLE_ADMIN role");
        Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN).orElseThrow();
        log.info("[Config] ROLE_ADMIN role found");
        
        log.info("[Config] Creating admin user entity");
        User admin = new User();
        admin.setFullName(fullName);
        admin.setEmail(email.toLowerCase());
        admin.setPhoneNumber(phone);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEmailVerified(true);
        admin.getRoles().add(adminRole);
        
        log.info("[Config] Saving admin user to database");
        userRepository.save(admin);
        log.info("[Config] Initial admin account created successfully for {}", email);
    }
}
