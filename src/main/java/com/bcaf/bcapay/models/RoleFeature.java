package com.bcaf.bcapay.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_features")
@Builder
public class RoleFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false,  referencedColumnName = "id")
    // @JsonIgnore
    private Role role;
    
    @ManyToOne
    @JoinColumn(name = "feature_id", nullable = false, referencedColumnName = "id")
    // @JsonIgnore
    private Feature feature;
}
