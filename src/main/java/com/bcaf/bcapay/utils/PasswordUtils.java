package com.bcaf.bcapay.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void verifyPasswordMatch(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new IllegalArgumentException("Wrong password!");
        }
    }

    public void isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        if (!hasUppercase || !hasLowercase || !hasDigit) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter, one lowercase letter, and one number.");
        }
    }
}
