package com.bcaf.finapay.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "id")
    private Role role;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = true, unique = true)
    private String nip;
    
    @Column(nullable = true, unique = true)
    private String refferal;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = true)
    @JsonBackReference
    private Branch branch;
}