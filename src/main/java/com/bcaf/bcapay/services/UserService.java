package com.bcaf.bcapay.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bcaf.bcapay.dto.UserDto;
import com.bcaf.bcapay.exceptions.ResourceNotFoundException;
import com.bcaf.bcapay.models.Role;
import com.bcaf.bcapay.models.User;
import com.bcaf.bcapay.repositories.RoleRepository;
import com.bcaf.bcapay.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan, silahkan registrasi terlebih dahulu"));
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
        userRepository.findByNip(user.getNip())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("NIP sudah digunakan user lain");
                });
        Optional<Role> role = roleRepository.findById(user.getRole().getId());

        if (role.isEmpty()) {
            throw new ResourceNotFoundException("Role tidak ditemukan!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    // Update user
    public UserDto updateUser(String id, User userDetails) {
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
