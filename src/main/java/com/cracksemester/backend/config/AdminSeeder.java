package com.cracksemester.backend.config;

import com.cracksemester.backend.entity.Role;
import com.cracksemester.backend.entity.User;
import com.cracksemester.backend.enums.RoleType;
import com.cracksemester.backend.repository.RoleRepository;
import com.cracksemester.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Order(2)
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@questionvault.com}")
    private String adminEmail;

    @Value("${app.admin.password:123456}")
    private String adminPassword;

    @Value("${app.admin.name:QuestionVault Admin}")
    private String adminName;

    public AdminSeeder(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN).build()));

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_USER).build()));

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        roles.add(userRole);

        User admin = userRepository.findByEmail(adminEmail).orElse(null);

        if (admin == null) {
            admin = User.builder()
                    .name(adminName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .branch("CSE")
                    .semester(8)
                    .enabled(true)
                    .roles(roles)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Admin user created successfully with email: " + adminEmail);
        } else {
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRoles(roles);
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("✅ Admin user password updated successfully for: " + adminEmail);
        }
    }
}
