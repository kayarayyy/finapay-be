package com.bcaf.bcapay.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcaf.bcapay.dto.AuthDto;
import com.bcaf.bcapay.models.Role;
import com.bcaf.bcapay.models.User;
import com.bcaf.bcapay.security.CustomUserDetails;
import com.bcaf.bcapay.utils.JwtUtil;
import com.bcaf.bcapay.utils.PasswordUtils;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    PasswordUtils passwordUtils;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthDto login(String email, String rawPassword) {
        User user = userService.getUserByEmail(email);

        passwordUtils.verifyPasswordMatch(rawPassword, user.getPassword());

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),
                        rawPassword));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> features = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = jwtUtil.generateToken(authentication);

        return new AuthDto(
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.isActive(),
                token,
                features);
    }

    public AuthDto login_employee(String nip, String rawPassword) {
        User user = userService.getUserByNip(nip);

        passwordUtils.verifyPasswordMatch(rawPassword, user.getPassword());

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),
                        rawPassword));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> features = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = jwtUtil.generateToken(authentication);

        return new AuthDto(
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.isActive(),
                token,
                features);
    }

    @Transactional
    public User register(Map<String, Object> payload, String token) {
        // Validasi input agar tidak null
        String email = Objects.toString(payload.get("email"), "").trim();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        boolean isSuperadmin = (token != null && jwtUtil.isSuperadmin(token)) ? true : false;

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Email tidak valid");
        }

        String name = Objects.toString(payload.get("name"), "").trim();
        String rawPassword = isSuperadmin ? rawPassword = RandomStringUtils.randomAlphanumeric(8)
                : Objects.toString(payload.get("password"), "").trim();
        String roleId = Objects.toString(payload.get("role_id"), "").trim();
        boolean isActive = Boolean.parseBoolean(Objects.toString(payload.get("is_active"), "false"));
        String nip = Objects.toString(payload.get("nip"), "").trim();
        String refferal = Objects.toString(payload.get("refferal"), "").trim();

        if (email.isEmpty() || name.isEmpty() || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Email, name, dan password tidak boleh kosong");
        }

        passwordUtils.isPasswordStrong(rawPassword);

        // Jika token null, default role adalah "customer"
        Role role = isSuperadmin
                ? roleService.getRoleById(roleId)
                : roleService.getRoleByName("CUSTOMER");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(rawPassword);
        user.setRole(role);
        user.setActive(isActive);
        user.setNip(nip);
        user.setRefferal(refferal);

        if (role.getName().equals("CUSTOMER")) {
            userService.createUser(user);

            emailService.sendCustomerRegistrationEmail(user);
        } else if (nip.isEmpty()) {
            throw new IllegalArgumentException("NIP tidak boleh kosong");
        } else {

            user.setPassword(rawPassword);
            userService.createUser(user);
            emailService.sendInitialPasswordEmail(email, rawPassword);
        }

        return user;

    }

    public User getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    public void changePassword(Map<String, Object> payload, String token) {
        String emailToken = jwtUtil.extractEmail(jwtUtil.trimToken(token));
        User user = userService.getUserByEmail(emailToken);
        String oldPassword = Objects.toString(payload.get("old_password")).trim();
        String newPassword = Objects.toString(payload.get("new_password")).trim();
        String confirmPassword = Objects.toString(payload.get("confirm_password")).trim();

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Konfirmasi password tidak sesuai!");
        }

        passwordUtils.isPasswordStrong(confirmPassword);
        passwordUtils.verifyPasswordMatch(oldPassword, user.getPassword());

        user.setPassword(newPassword);

        userService.updateUser(user.getId().toString(), user);
    }

}
