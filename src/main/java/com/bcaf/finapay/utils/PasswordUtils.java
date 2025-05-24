package com.bcaf.finapay.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void verifyPasswordMatch(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new IllegalArgumentException("Password lama tidak sesuai!");
        }
    }

    public void isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password minimal memiliki 8 karakter");
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        if (!hasUppercase || !hasLowercase || !hasDigit) {
            throw new IllegalArgumentException(
                    "Password harus berisi minimal 1 huruf kapital, 1 huruf kecil dan 1 angka");
        }
    }
}
