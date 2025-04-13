package com.bcaf.bcapay.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bcaf.bcapay.dto.ResponseDto;
import com.bcaf.bcapay.models.Feature;
import com.bcaf.bcapay.services.FeatureService;

@Controller
@RequestMapping("api/v1/features")
public class FeatureController {
    @Autowired
    private FeatureService featureService;

    @Secured("FEATURE_MANAGE_FEATURES")
    @GetMapping
    public ResponseEntity<ResponseDto> getAllFeatures() {
        List<Feature> features = featureService.getAllFeatures();
        return ResponseEntity.ok(new ResponseDto<>(200, "success", features.size() + " features found", features));
    }

    @Secured("FEATURE_MANAGE_FEATURES")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getFeatureById(@PathVariable String id) {
        Feature feature = featureService.getFeatureById(id);
        return ResponseEntity.ok(new ResponseDto<>(200, "success", "Feature found", feature));
    }
    
    @Secured("FEATURE_MANAGE_FEATURES")
    @PostMapping
    public ResponseEntity<ResponseDto> createFeature(@RequestBody Feature feature) {
        Feature createdFeature = featureService.creaFeature(feature);
        return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ResponseDto<>(201, "success", "Feature created", createdFeature));
    }
    
    @Secured("FEATURE_MANAGE_FEATURES")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateFeature(@PathVariable String id, Feature feature) {
        Feature updatedFeature = featureService.updateFeature(id,feature);
        return ResponseEntity.ok(new ResponseDto<>(200, "success", "Feature updated", feature));
    }

    @Secured("FEATURE_MANAGE_FEATURES")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteFeature(@PathVariable String id) {
        featureService.deleteFeature(id);
        return ResponseEntity.ok(new ResponseDto<>(200, "success", "Feature deleted", null));
    }
}
