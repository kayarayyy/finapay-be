package com.bcaf.finapay.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bcaf.finapay.exceptions.ResourceNotFoundException;
import com.bcaf.finapay.models.ResetPassword;
import com.bcaf.finapay.models.User;
import com.bcaf.finapay.repositories.ResetPasswordRepository;
import com.bcaf.finapay.utils.PasswordUtils;

import jakarta.transaction.Transactional;

@Service
public class ResetPasswordService {
    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordUtils passwordUtils;

    public List<ResetPassword> getAllResetPasswordToken() {
        List<ResetPassword> listResetPassword = resetPasswordRepository.findAll();

        return listResetPassword;
    }

    @Transactional
    public void getResetPasswordLink(String email) {
        User user = userService.getUserByEmail(email);

        Optional<ResetPassword> existingReset = resetPasswordRepository.findByUserEmailAndExpiredAtAfter(email,
                LocalDateTime.now());

        ResetPassword resetPassword;
        if (existingReset.isPresent()) {
            resetPassword = existingReset.get();
            resetPassword.setExpiredAt(LocalDateTime.now().plusHours(24));
        } else {
            resetPassword = new ResetPassword();
            resetPassword.setUser(user);
            resetPassword.setExpiredAt(LocalDateTime.now().plusHours(24));
        }

        resetPassword = resetPasswordRepository.save(resetPassword);
        emailService.sendRequestResetPassword(user.getName(), user.getEmail(), resetPassword.getId().toString());
    }

    @Transactional
    public void setNewPasswordByResetPasswordEmail(String token, String email, String newPassword,
            String confirmPassword) {

        UUID resetPasswordId = null;
        try {
            resetPasswordId = UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Token tidak valid!");
        }
        ResetPassword resetPassword = resetPasswordRepository.findByIdAndUserEmail(resetPasswordId, email)
                .orElseThrow(() -> new ResourceNotFoundException("User atau token tidak ditemukan"));
        if (resetPassword.getExpiredAt().isBefore(LocalDateTime.now())) {
            resetPasswordRepository.delete(resetPassword);
            throw new IllegalStateException("Token sudah kedaluwarsa!");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Konfirmasi password tidak sesuai!");
        }

        passwordUtils.isPasswordStrong(confirmPassword);

        User user = resetPassword.getUser();
        user.setPassword(newPassword);
        userService.changePassword(user.getId().toString(), user);

        resetPasswordRepository.delete(resetPassword);
    }

    public void deleteResetPasswordToken(UUID id) {
        resetPasswordRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found!"));

        resetPasswordRepository.deleteById(id);
    }

}
