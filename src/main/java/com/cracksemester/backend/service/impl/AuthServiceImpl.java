package com.cracksemester.backend.service.impl;

import com.cracksemester.backend.dto.*;
import com.cracksemester.backend.entity.Role;
import com.cracksemester.backend.entity.User;
import com.cracksemester.backend.enums.RoleType;
import com.cracksemester.backend.repository.FavoriteRepository;
import com.cracksemester.backend.repository.DownloadRepository;
import com.cracksemester.backend.repository.RoleRepository;
import com.cracksemester.backend.repository.UserRepository;
import com.cracksemester.backend.security.JwtTokenProvider;
import com.cracksemester.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FavoriteRepository favoriteRepository;
    private final DownloadRepository downloadRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists!");
        }

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_USER).build()));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .branch(request.getBranch())
                .semester(request.getSemester())
                .enabled(true)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        String token = tokenProvider.generateTokenFromEmail(user.getEmail());
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToUserDTO(user))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = tokenProvider.generateToken(authentication);
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToUserDTO(user))
                .build();
    }

    @Override
    public UserDTO getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapToUserDTO(user);
    }

    private UserDTO mapToUserDTO(User user) {
        String roleStr = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleType.ROLE_ADMIN) ? "ADMIN" : "STUDENT";

        Long favCount = favoriteRepository.countByUserId(user.getId());
        Long dlCount = downloadRepository.countByUserId(user.getId());

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .branch(user.getBranch() != null ? user.getBranch() : "CSE")
                .semester(user.getSemester() != null ? user.getSemester() : 3)
                .role(roleStr)
                .enabled(user.getEnabled())
                .avatar("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150")
                .bookmarksCount(favCount != null ? favCount : 0L)
                .downloadsCount(dlCount != null ? dlCount : 0L)
                .joinedDate(user.getCreatedAt() != null ? user.getCreatedAt().toLocalDate().toString() : "2025-01-01")
                .build();
    }
}
