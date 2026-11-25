package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse signin(LoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);
    UserResponse me();
    void changePassword(ChangePasswordRequest request);
}
