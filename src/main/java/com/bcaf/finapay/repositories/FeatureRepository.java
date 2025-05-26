package com.bcaf.finapay.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bcaf.finapay.models.Feature;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, UUID> {
    Optional<Feature> findByName(String name);

    @Query("SELECT f FROM Feature f LEFT JOIN FETCH f.roleFeatures WHERE f.id = :id")
    Optional<Feature> findByIdWithRoleFeatures(@Param("id") UUID id);

}