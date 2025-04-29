package com.bcaf.bcapay.dto;

import java.util.UUID;

import com.bcaf.bcapay.models.User;

public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private String nip;
    private String refferal;
    private boolean active;
    private String branch;

    public UserDto(UUID id, String name, String email, String role, String nip, String refferal,boolean active, String branch) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.nip = nip;
        this.refferal = refferal;
        this.active = active;
        this.branch = branch;
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive(){
        return active;
    }

    public String getRefferal(){
        return refferal;
    }

    public String getNip(){
        return nip;
    }
    public String getBranch(){
        return branch;
    }

    // Convert from User entity to DTO
    public static UserDto fromEntity(User user) {
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole() != null ? user.getRole().getName() : null,
            user.getNip(),
            user.getRefferal(),
            user.isActive(),
            user.getBranch() != null ? user.getBranch().getName() : null
        );
    }
}
