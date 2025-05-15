package com.bcaf.bcapay.models;

import java.time.LocalDate;
import java.util.UUID;

import com.bcaf.bcapay.models.enums.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_details")
public class CustomerDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "available_plafond")
    private double availablePlafond;

    @ManyToOne
    @JoinColumn(name = "plafond_id", referencedColumnName = "id", nullable = false)
    private Plafond plafondPlan;

    @Column(name = "street")
    private String street;

    @Column(name = "district")
    private String district;

    @Column(name = "province")
    private String province;

    @Column(name = "postal_code")
    private String postalCode;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate ttl;

    @Column(name = "ktp_url")
    private String ktpUrl;

    @Column(name = "selfie_ktp_url")
    private String selfieKtpUrl;
    
    private String noTelp;

    private String nik;

    @Column(name = "mothers_name")
    private String mothersName;
    
    private String job;
    
    private Double salary;
    
    @Column(name = "no_rek")
    private String noRek;
    
    @Column(name = "house_url")
    private String houseUrl;

    @Column(name = "house_status")
    private String houseStatus;

    @OneToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false, unique = true)
    private User user;

}
