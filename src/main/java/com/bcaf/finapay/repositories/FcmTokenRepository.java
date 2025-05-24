package com.bcaf.finapay.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcaf.finapay.models.FcmToken;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, UUID>{
    Optional<FcmToken> findByToken(String token);
    List<FcmToken> findByEmail(String email);
    void deleteByToken(String token);
    void deleteByEmail(String email);
}
