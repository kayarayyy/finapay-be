package com.bcaf.finapay.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.websocket.AuthenticationException;
import org.hibernate.usertype.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcaf.finapay.dto.AuthDto;
import com.bcaf.finapay.dto.UserDto;
import com.bcaf.finapay.exceptions.ResourceNotFoundException;
import com.bcaf.finapay.models.Role;
import com.bcaf.finapay.models.User;
import com.bcaf.finapay.repositories.UserRepository;
import com.bcaf.finapay.security.CustomUserDetails;
import com.bcaf.finapay.utils.GoogleTokenVerifier;
import com.bcaf.finapay.utils.JwtUtil;
import com.bcaf.finapay.utils.PasswordUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

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

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FcmTokenServices fcmTokenServices;

    public AuthDto login(String email, String rawPassword, String fcmToken) {
        User user = userService.getUserByEmail(email);
        if (!user.isActive()) {
            throw new AccessDeniedException(
                    "Akun Anda belum aktif. Silakan periksa email Anda dan ikuti tautan aktivasi yang telah dikirimkan saat registrasi.");
        }

        if (!user.getRole().getName().equalsIgnoreCase("CUSTOMER")) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk login");
        }

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
        if (fcmToken != null) {
            fcmTokenServices.saveToken(email, fcmToken);
        }

        return new AuthDto(
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.isActive(),
                token,
                features);
    }

    public AuthDto login_with_google(String idToken, String fcmToken) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(idToken);

        if (payload == null) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // Buat akun baru
            Role customerRole = roleService.getRoleByName("CUSTOMER");
            String rawPassword = RandomStringUtils.randomAlphanumeric(8); // password dummy

            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPassword(passwordEncoder.encode(rawPassword)); // password dummy
            user.setRole(customerRole);
            user.setActive(true);

            user = userRepository.save(user);
            emailService.sendCustomerGoogleRegistrationEmail(user, rawPassword);
        }

        if (!user.getRole().getName().equalsIgnoreCase("CUSTOMER")) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk login");
        }

        // Set autentikasi manual tanpa pakai password
        CustomUserDetails userDetails = (CustomUserDetails) CustomUserDetails.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> features = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = jwtUtil.generateToken(authentication);
        if (fcmToken != null) {
            fcmTokenServices.saveToken(email, fcmToken);
        }

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
    public void generateActivationLink(String email) {
        User user = userService.getUserByEmail(email);
        if (user.isActive()) {
            throw new IllegalArgumentException("Akun telah teraktivasi, silahkan login");
        }

        emailService.sendActivationLink(user);
    }

    public void activate(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        user.setActive(true);
        userRepository.save(user);
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
        String rawPassword = isSuperadmin ? RandomStringUtils.randomAlphanumeric(8)
                : Objects.toString(payload.get("password"), "").trim();
        String roleId = Objects.toString(payload.get("role_id"), "").trim();
        boolean isActive = Boolean.parseBoolean(Objects.toString(payload.get("is_active"), "true"));
        String nip = Objects.toString(payload.get("nip"), "").trim();
        String refferal = Objects.toString(payload.get("refferal"), "").trim();

        if (email.isEmpty() || name.isEmpty() || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Email, name, dan password tidak boleh kosong");
        }

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
            user.setNip(null);
            user.setRefferal(null);
            user.setActive(false);
            passwordUtils.isPasswordStrong(rawPassword);
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

        passwordUtils.verifyPasswordMatch(oldPassword, user.getPassword());
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Konfirmasi password tidak sesuai!");
        }

        passwordUtils.isPasswordStrong(confirmPassword);

        user.setPassword(newPassword);

        userService.changePassword(user.getId().toString(), user);
    }

}
