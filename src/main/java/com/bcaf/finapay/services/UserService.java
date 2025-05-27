package com.bcaf.finapay.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bcaf.finapay.dto.UserDto;
import com.bcaf.finapay.exceptions.ResourceNotFoundException;
import com.bcaf.finapay.models.Role;
import com.bcaf.finapay.models.User;
import com.bcaf.finapay.repositories.RoleRepository;
import com.bcaf.finapay.repositories.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }

    // Get user by ID
    public UserDto getUserById(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        return UserDto.fromEntity(user);
    }

    // return User with password for auth
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User tidak ditemukan, silahkan registrasi terlebih dahulu"));
    }

    public User getUserByRefferal(String refferal) {
        return userRepository.findByRefferal(refferal)
                .orElseThrow(() -> new ResourceNotFoundException("Refferal tidak ditemukan"));
    }

    // return User with password for auth
    public User getUserByNip(String nip) {
        return userRepository.findByNip(nip)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    public boolean getUserByEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Create a new user
    public UserDto createUser(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Email sudah digunakan user lain");
                });
        if (user.getNip() != null) {
            userRepository.findByNip(user.getNip())
                    .ifPresent(u -> {
                        throw new IllegalArgumentException("NIP sudah digunakan user lain");
                    });
        }
        Optional<Role> role = roleRepository.findById(user.getRole().getId());

        if (role.isEmpty()) {
            throw new ResourceNotFoundException("Role tidak ditemukan!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    public UserDto updateUser(String id, Map<String, Object> payload) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan!"));
        String email = Objects.toString(payload.get("email"), "").trim();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Email tidak valid");
        }
        String name = Objects.toString(payload.get("name"), "").trim();
        String roleId = Objects.toString(payload.get("role_id"), "").trim();
        boolean isActive = Boolean.parseBoolean(Objects.toString(payload.get("is_active"), "true"));
        String nip = Objects.toString(payload.get("nip"), "").trim();
        String refferal = Objects.toString(payload.get("refferal"), "").trim();

        user.setName(name);
        user.setEmail(email);
        user.setActive(isActive);
        user.setNip(nip);
        user.setRefferal(refferal);
        Optional<Role> role = roleRepository.findById(UUID.fromString(roleId));
        if (role.isEmpty()) {
            throw new ResourceNotFoundException("Role not found!");
        }
        user.setRole(role.get());

        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    // Update user
    public UserDto changePassword(String id, User userDetails) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan!"));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        user.setActive(userDetails.isActive());
        user.setNip(userDetails.getNip());
        Optional<Role> role = roleRepository.findById(userDetails.getRole().getId());
        if (role.isEmpty()) {
            throw new ResourceNotFoundException("Role not found!");
        }
        user.setRole(role.get());

        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    // Delete user
    public void deleteUser(String id) {
        userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        userRepository.deleteById(UUID.fromString(id));
    }
}
