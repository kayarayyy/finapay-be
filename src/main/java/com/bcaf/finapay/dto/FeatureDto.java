
package com.bcaf.finapay.dto;

import java.util.UUID;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeatureDto {
    private UUID id;
    private String name;
}