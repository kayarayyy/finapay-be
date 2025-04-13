package com.bcaf.bcapay.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcaf.bcapay.exceptions.ResourceNotFoundException;
import com.bcaf.bcapay.models.Feature;
import com.bcaf.bcapay.repositories.FeatureRepository;

@Service
public class FeatureService {
    @Autowired
    private FeatureRepository featureRepository;

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

    public void deleteFeature(String id) {
        featureRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found!"));

        featureRepository.deleteById(UUID.fromString(id));
    }
}
