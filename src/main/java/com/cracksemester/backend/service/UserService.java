package com.cracksemester.backend.service;

import com.cracksemester.backend.dto.AuthResponse;
import com.cracksemester.backend.dto.RegisterRequest;
import com.cracksemester.backend.dto.UserDTO;
import com.cracksemester.backend.entity.User;
import com.cracksemester.backend.repository.UserRepository;
import com.cracksemester.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists!";
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .branch(request.getBranch())
                .semester(request.getSemester())
                .enabled(true)
                .build();

        userRepository.save(user);
        return "Registration Successful";
    }

    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return AuthResponse.builder()
                    .token(null)
                    .tokenType("Bearer")
                    .build();
        }

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(UserDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .branch(user.getBranch())
                        .semester(user.getSemester())
                        .role("STUDENT")
                        .build())
                .build();
    }
}