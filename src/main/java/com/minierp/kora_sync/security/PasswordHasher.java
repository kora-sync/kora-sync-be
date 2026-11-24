package com.minierp.kora_sync.security;

import com.minierp.kora_sync.exception.InvalidInputException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PasswordHasher {

    private static final int BCRYPT_MAX_BYTES = 72;

    private final PasswordEncoder passwordEncoder;
    private final String dummyHash;

    public PasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.dummyHash = passwordEncoder.encode("kora-sync-dummy-password");
    }

    public String hash(String rawPassword) {
        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
            throw new InvalidInputException("The password must not exceed 72 bytes");
        }
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String storedHash) {
        boolean known = storedHash != null;
        boolean matches = passwordEncoder.matches(rawPassword, known ? storedHash : dummyHash);
        return known && matches;
    }
}