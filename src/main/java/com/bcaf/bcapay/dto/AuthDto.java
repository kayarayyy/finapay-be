package com.bcaf.bcapay.dto;


import java.util.List;

import com.bcaf.bcapay.models.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {

    private String email;
    private String name;
    private Role role;
    @JsonProperty("is_active")
    private boolean isActive;
    private String token;
    private List<String> features;
}
