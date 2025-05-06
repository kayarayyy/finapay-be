package com.bcaf.bcapay.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcaf.bcapay.models.Plafond;

@Repository
public interface PlafondRepository extends JpaRepository<Plafond, UUID> {
    Optional<Plafond> findByPlan(String plan);
    boolean existsByPlan(String plan);
}