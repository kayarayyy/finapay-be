package com.bcaf.bcapay.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcaf.bcapay.models.RoleFeature;

@Repository
public interface RoleFeatureRepository extends JpaRepository<RoleFeature, UUID> {
    List<RoleFeature> findByRoleId(UUID roleId);
}