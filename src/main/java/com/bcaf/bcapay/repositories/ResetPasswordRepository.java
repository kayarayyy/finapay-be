package com.bcaf.bcapay.repositories;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bcaf.bcapay.models.ResetPassword;

import jakarta.transaction.Transactional;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, UUID> {

    Optional<ResetPassword> findByUserEmailAndExpiredAtAfter(String email, LocalDateTime now);
    Optional<ResetPassword> findByIdAndUserEmail(UUID id, String email);

}
