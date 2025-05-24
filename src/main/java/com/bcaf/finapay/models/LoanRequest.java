package com.bcaf.finapay.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.bcaf.finapay.models.enums.LoanStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "loan_request")
public class LoanRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = true)
    private Double interest;

    @Column(nullable = true)
    private Double adminFee;

    @Column(nullable = true)
    private String refferal;

    @Column(nullable = false)
    private int tenor;

    @ManyToOne
    @JoinColumn(name = "branch", referencedColumnName = "city")
    @JsonBackReference
    private Branch branch;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "customer_email", referencedColumnName = "email")
    @JsonManagedReference
    private User customer;

    @ManyToOne
    @JoinColumn(name = "marketing_email", referencedColumnName = "email")
    @JsonManagedReference
    private User marketing;

    @Column(nullable = true)
    private Boolean marketingApprove;

    @Column(nullable = true)
    private String marketingNotes;

    @Column(nullable = true)
    private LocalDateTime marketingReviewedAt;

    @ManyToOne
    @JoinColumn(name = "branch_manager_email", referencedColumnName = "email")
    @JsonManagedReference
    private User branchManager;

    @Column(nullable = true)
    private Boolean branchManagerApprove;

    @Column(nullable = true)
    private String branchManagerNotes;

    @Column(nullable = true)
    private LocalDateTime branchManagerApprovedAt;

    @ManyToOne
    @JoinColumn(name = "back_office_email", referencedColumnName = "email")
    @JsonManagedReference
    private User backOffice;

    @Column(nullable = true)
    private Boolean backOfficeApproveDisburse;

    @Column(nullable = true)
    private String backOfficeNotes;

    @Column(nullable = true)
    private LocalDateTime backOfficeDisbursedAt;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;
}
