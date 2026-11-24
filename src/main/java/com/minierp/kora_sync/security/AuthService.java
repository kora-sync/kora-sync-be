package com.minierp.kora_sync.security;

import com.minierp.kora_sync.dto.AuthResponse;
import com.minierp.kora_sync.dto.LoginRequest;
import com.minierp.kora_sync.dto.RegisterRequest;
import com.minierp.kora_sync.dto.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse signin(LoginRequest request);
    UserResponse me();
}
