package com.bcaf.finapay.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcaf.finapay.models.Plafond;

@Repository
public interface PlafondRepository extends JpaRepository<Plafond, UUID> {
    Optional<Plafond> findByPlan(String plan);
    boolean existsByPlan(String plan);
    List<Plafond> findAllByOrderByAmountAsc();

}