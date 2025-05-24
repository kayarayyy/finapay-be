package com.bcaf.finapay.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcaf.finapay.models.Feature;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, UUID> {
    Optional<Feature> findByName(String name);
}