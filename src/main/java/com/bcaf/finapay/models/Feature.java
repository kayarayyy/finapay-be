package com.bcaf.finapay.models;

import java.util.List;
import java.util.Set;
import java.util.UUID;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "features")
@Builder
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name; // Misalnya: "CREATE_USER", "APPROVE_LOAN", dll.

    @OneToMany(mappedBy = "feature", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RoleFeature> roleFeatures;

    // @ManyToOne
    // @JoinColumn(name = "feature_group_id")
    // private FeatureGroup featureGroup;
}