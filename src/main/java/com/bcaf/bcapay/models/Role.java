package com.bcaf.bcapay.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")

@Builder
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    @Column(name = "name", nullable = false, unique = true)
    // @JsonIgnore
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RoleFeature> roleFeatures = new ArrayList<>();

    // Constructor hanya dengan name
    public Role(String name) {
        this.name = name;
    }
}
