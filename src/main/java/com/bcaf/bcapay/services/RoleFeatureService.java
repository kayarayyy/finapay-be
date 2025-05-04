package com.bcaf.bcapay.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcaf.bcapay.dto.FeatureDto;
import com.bcaf.bcapay.dto.RoleFeaturesDto;
import com.bcaf.bcapay.exceptions.ResourceNotFoundException;
import com.bcaf.bcapay.models.Feature;
import com.bcaf.bcapay.models.Role;
import com.bcaf.bcapay.models.RoleFeature;
import com.bcaf.bcapay.repositories.RoleFeatureRepository;

@Service
public class RoleFeatureService {
    @Autowired
    private RoleFeatureRepository roleFeatureRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private FeatureService featureService;

    // public List<RoleFeature> getAllRoleFeatures() {
    // return roleFeatureRepository.findAll();
    // }

    public RoleFeature getRoleFeatureById(String id) {
        return roleFeatureRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Role Feature not found!"));
    }

    public RoleFeature createRoleFeature(Map<String, Object> payload) {
        String roleId = Objects.toString(payload.get("role_id"), "").trim();
        String featureId = Objects.toString(payload.get("feature_id"), "").trim();
        Role role = roleService.getRoleById(roleId);
        Feature feature = featureService.getFeatureById(featureId);

        RoleFeature roleFeature = new RoleFeature(null, role, feature);
        return roleFeatureRepository.save(roleFeature);
    }

    public void deleteRoleFeature(UUID roleId, UUID featureId) {

        int deleted = roleFeatureRepository.deleteByRoleIdAndFeatureId(roleId, featureId);
        if (deleted == 0) {
            throw new ResourceNotFoundException("Role Feature not found!");
        }
    }

    public List<FeatureDto> getFeaturesByRoleId(UUID roleId) {
        List<RoleFeature> roleFeatures = roleFeatureRepository.findByRoleId(roleId);

        return roleFeatures.stream()
                .map(rf -> FeatureDto.builder()
                        .id(rf.getFeature().getId())
                        .name(rf.getFeature().getName())
                        .build())
                .collect(Collectors.toList());
    }

    public List<RoleFeaturesDto> getAllRoleWithFeatures() {
        return roleService.getAllRoles().stream().map(role -> {
            List<FeatureDto> features = getFeaturesByRoleId(role.getId());

            return RoleFeaturesDto.builder()
                    .id(role.getId())
                    .name(role.getName())
                    .listFeatures(features)
                    .build();
        }).collect(Collectors.toList());
    }

}
