package com.bcaf.finapay.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.bcaf.finapay.models.Role;
import com.bcaf.finapay.repositories.RoleRepository;
import com.bcaf.finapay.security.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int expiration;

    @Autowired
    private RoleRepository roleRepository;

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(Authentication authentication) {
        String username;
        String roleId;
        if (authentication.getPrincipal() instanceof CustomUserDetails userPrincipal) {
            username = userPrincipal.getUsername();
            roleId = userPrincipal.getUser().getRole().getId().toString();
        } else {
            throw new IllegalArgumentException("Unsupported principal type");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("roleId", roleId);
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(calendar.HOUR, expiration);
        Date expiredDate = calendar.getTime();
        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(trimToken(token))
                .getBody()
                .getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String getUsername(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    public String trimToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return "";
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return token;
    }


    public boolean isSuperadmin(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;// Jika tidak ada token, bukan superadmin
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = extractAllClaims(token);
        String roleId = claims.get("roleId", String.class);
        Optional<Role> role = roleRepository.findById(UUID.fromString(roleId));

        return role.isPresent() && role.get().getName().equals("SUPERADMIN");
    }

    public String getRoleIdFromToken() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }
}
