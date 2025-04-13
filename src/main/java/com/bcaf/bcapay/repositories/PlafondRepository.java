package com.bcaf.bcapay.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcaf.bcapay.models.Plafond;
import com.bcaf.bcapay.models.enums.Plan;

@Repository
public interface PlafondRepository extends JpaRepository<Plafond, UUID> {
    Optional<Plafond> findByPlan(Plan plan);
    boolean existsByPlan(Plan plan);
}