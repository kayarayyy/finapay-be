package com.bcaf.finapay.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcaf.finapay.models.CustomerDetails;
import com.bcaf.finapay.models.User;

@Repository
public interface CustomerDetailsRepository extends JpaRepository<CustomerDetails, UUID> {
    Optional<CustomerDetails> findByUserEmail(String email);
    boolean existsByUser(User user);

}

