package com.bcaf.bcapay.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcaf.bcapay.models.EmployeeDetails;

@Repository
public interface EmployeeDetailsRepoitory extends JpaRepository<EmployeeDetails, UUID> {
    Optional<EmployeeDetails> findByUserEmail(String email);
}
