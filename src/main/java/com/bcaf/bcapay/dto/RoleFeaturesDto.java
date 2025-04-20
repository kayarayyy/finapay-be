package com.bcaf.bcapay.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleFeaturesDto {
    private UUID id;
    private String name;
    private List<FeatureDto> listFeatures;
}
