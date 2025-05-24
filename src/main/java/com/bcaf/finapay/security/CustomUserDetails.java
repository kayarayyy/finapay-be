package com.bcaf.finapay.security;

import jakarta.transaction.Transactional;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bcaf.finapay.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final User user;
    private final String email;
    private final String password;
    private final boolean isActive;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(User user, List<GrantedAuthority> authorities) {
        this.user = user;
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.isActive = user.isActive();
        
        // Gunakan unmodifiableSet agar tidak terjadi ConcurrentModificationException
        this.authorities = authorities;
    }

    @Transactional
    public static UserDetails build(User user) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if(user.getRole() != null) {
            // Combine role-based authorities and feature-based authorities
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
            // Extract features from the role
            if (user.getRole().getRoleFeatures() != null) {
                user.getRole().getRoleFeatures().forEach(roleFeature ->
                grantedAuthorities.add(new SimpleGrantedAuthority("FEATURE_" + roleFeature.getFeature().getName()))
                );
            }
        }

        return new CustomUserDetails(user, grantedAuthorities);
    }

    public List<String> getFeatures() {
        List<String> features = new ArrayList<>();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().startsWith("FEATURE_")) {
                features.add(authority.getAuthority());
            }
        }
        return features;
    }
    

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
