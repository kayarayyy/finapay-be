package com.bcaf.bcapay.models;

import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "plafond")
public class Plafond {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private double amount;

    @Column(name = "plan_type", nullable = false, unique = true)
    private String plan;

    @Column(nullable = false)
    private Double annualRate;

    @Column(nullable = true)
    private String colorStart;

    @Column(nullable = true)
    private String colorEnd;

    @PrePersist
    public void prePersist() {
        if (this.colorStart == null) {
            this.colorStart = "#C0C0C0"; // silver terang
        }
        if (this.colorEnd == null) {
            this.colorEnd = "#808080"; // abu gelap
        }
    }

}
