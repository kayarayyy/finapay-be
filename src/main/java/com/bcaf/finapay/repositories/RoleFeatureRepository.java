package com.bcaf.finapay.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bcaf.finapay.models.RoleFeature;

import jakarta.transaction.Transactional;

@Repository
public interface RoleFeatureRepository extends JpaRepository<RoleFeature, UUID> {
    List<RoleFeature> findByRoleId(UUID roleId);

    Optional<RoleFeature> findByRoleIdAndFeatureId(UUID roleId, UUID featureId);

    @Transactional
    @Modifying
    @Query("DELETE FROM RoleFeature rf WHERE rf.role.id = :roleId AND rf.feature.id = :featureId")
    int deleteByRoleIdAndFeatureId(@Param("roleId") UUID roleId, @Param("featureId") UUID featureId);

    @Modifying
    @Query("DELETE FROM RoleFeature rf WHERE rf.feature.id = :featureId")
    void deleteAllByFeatureId(@Param("featureId") UUID featureId);

}