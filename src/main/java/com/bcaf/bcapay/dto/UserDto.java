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

    public UserDto(UUID id, String name, String email, String role, String nip, String refferal,boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.nip = nip;
        this.refferal = refferal;
        this.active = active;
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

    // Convert from User entity to DTO
    public static UserDto fromEntity(User user) {
        return new UserDto(
            user.getId(),  // Ambil ID user
            user.getName(),
            user.getEmail(),
            user.getRole().getName(),
            user.getNip(),
            user.getRefferal(),
            user.isActive()
        );
    }
}
