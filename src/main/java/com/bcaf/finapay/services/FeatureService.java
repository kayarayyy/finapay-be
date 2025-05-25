package com.bcaf.finapay.services;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcaf.finapay.exceptions.ResourceNotFoundException;
import com.bcaf.finapay.models.Feature;
import com.bcaf.finapay.models.RoleFeature;
import com.bcaf.finapay.repositories.FeatureRepository;
import com.bcaf.finapay.repositories.RoleFeatureRepository;

import jakarta.transaction.Transactional;

@Service
public class FeatureService {
    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private RoleFeatureRepository roleFeatureRepository;

    public List<Feature> getAllFeatures() {
        return featureRepository.findAll();
    }

    public Feature getFeatureById(String id) {
        return featureRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found!"));
    }

    public Feature creaFeature(Feature feature) {
        return featureRepository.save(feature);
    }

    public Feature updateFeature(String id, Feature updatedFeature) {
        Feature feature = featureRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found!"));

        feature.setName(updatedFeature.getName());
        return featureRepository.save(updatedFeature);
    }

    @Transactional
    public void deleteFeature(String id) {
        UUID uuid = UUID.fromString(id);

        // pastikan feature ada
        featureRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found!"));

        // hapus semua role-feature relasi
        roleFeatureRepository.deleteAllByFeatureId(uuid);

        // hapus feature
        featureRepository.deleteById(uuid);
    }

}
