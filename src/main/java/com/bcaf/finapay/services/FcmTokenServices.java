package com.bcaf.finapay.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcaf.finapay.models.FcmToken;
import com.bcaf.finapay.repositories.FcmTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class FcmTokenServices {
    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    // Create or Update (Save new token for user)
    public FcmToken saveToken(String email, String token) {
        Optional<FcmToken> existingToken = fcmTokenRepository.findByToken(token);

        if (existingToken.isPresent()) {
            FcmToken tokenToUpdate = existingToken.get();
            tokenToUpdate.setEmail(email); // update email jika perlu
            return fcmTokenRepository.save(tokenToUpdate);
        }

        FcmToken newToken = FcmToken.builder()
                .email(email)
                .token(token)
                .build();

        return fcmTokenRepository.save(newToken);
    }

    // Get all tokens by email
    public List<FcmToken> getTokensByEmail(String email) {
        return fcmTokenRepository.findByEmail(email);
    }

    // Delete a specific token (e.g., on logout from a device)
    @Transactional
    public void deleteToken(String token) {
        fcmTokenRepository.deleteByToken(token);
    }

    // Delete all tokens for an email (optional use case)
    public void deleteAllTokensByEmail(String email) {
        fcmTokenRepository.deleteByEmail(email);
    }

    // Get all tokens (optional - for admin)
    public List<FcmToken> getAllTokens() {
        return fcmTokenRepository.findAll();
    }
}
