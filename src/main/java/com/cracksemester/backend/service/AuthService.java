package com.cracksemester.backend.service;

import com.cracksemester.backend.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserDTO getMe(String email);
}
