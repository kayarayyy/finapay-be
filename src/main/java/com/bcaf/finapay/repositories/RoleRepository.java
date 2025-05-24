package com.bcaf.finapay.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcaf.finapay.models.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    
    boolean existsByName(String name);

    Optional<Role> findByName(String string);
}
