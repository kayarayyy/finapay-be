package com.bcaf.finapay.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcaf.finapay.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String name);
    Optional<User> findByEmail(String email);
    Optional<User> findByNip(String nip);
    Optional<User> findByRefferal(String refferal);
}
