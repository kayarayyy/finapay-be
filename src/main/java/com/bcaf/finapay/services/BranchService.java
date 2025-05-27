package com.bcaf.finapay.services;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcaf.finapay.dto.BranchDto;
import com.bcaf.finapay.exceptions.ResourceNotFoundException;
import com.bcaf.finapay.models.Branch;
import com.bcaf.finapay.models.User;
import com.bcaf.finapay.models.enums.City;
import com.bcaf.finapay.repositories.BranchRepository;
import com.bcaf.finapay.repositories.UserRepository;
import com.bcaf.finapay.utils.LocationCheck;

@Service
public class BranchService {
    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationCheck locationCheck;

    public List<BranchDto> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(BranchDto::fromEntity)
                .collect(Collectors.toList());
    }

    public BranchDto getBranchById(String id) {
        Branch branch = branchRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found!"));

        return BranchDto.fromEntity(branch);
    }

    public Branch getBranchByBranchManagerId(UUID id) {
        Branch branch = branchRepository.findByBranchManagerId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found!"));

        return branch;
    }

    public Branch createBranch(Map<String, Object> payload) {
        String name = Objects.toString(payload.get("name"), "").trim();
        String cityName = Objects.toString(payload.get("city"), "").trim().toUpperCase();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Branch name cannot be empty!");
        }

        if (branchRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Branch name '" + name + "' already exists!");
        }

        City city;
        try {
            city = City.valueOf(cityName);
        } catch (IllegalArgumentException e) {
            String validCities = Arrays.stream(City.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Invalid city name: " + cityName +
                    ". Valid city names are: " + validCities);
        }

        Double latitude = parseDouble(payload.get("latitude"), "latitude");
        Double longitude = parseDouble(payload.get("longitude"), "longitude");

        Branch branch = new Branch();
        branch.setName(name);
        branch.setCity(city);
        branch.setLatitude(latitude);
        branch.setLongitude(longitude);

        return branchRepository.save(branch);
    }

    private Double parseDouble(Object value, String fieldName) {
        if (value == null)
            return null;
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + fieldName + ": must be a valid coordinat.");
        }
    }

    public Branch editBranch(String id, Map<String, Object> payload) {
        Branch branch = branchRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Branch not found with ID: " + id));

        String name = Objects.toString(payload.get("name"), "").trim();
        String cityName = Objects.toString(payload.get("city"), "").trim().toUpperCase();

        if (!name.isEmpty()) {
            branch.setName(name);
        }

        if (!cityName.isEmpty()) {
            try {
                City city = City.valueOf(cityName);
                branch.setCity(city);
            } catch (IllegalArgumentException e) {
                String validCities = Arrays.stream(City.values())
                        .map(Enum::name)
                        .collect(Collectors.joining(", "));
                throw new IllegalArgumentException("Invalid city name: " + cityName +
                        ". Valid city names are: " + validCities);
            }
        }

        if (payload.containsKey("latitude")) {
            Double latitude = parseDouble(payload.get("latitude"), "latitude");
            branch.setLatitude(latitude);
        }

        if (payload.containsKey("longitude")) {
            Double longitude = parseDouble(payload.get("longitude"), "longitude");
            branch.setLongitude(longitude);
        }

        return branchRepository.save(branch);
    }

    public void deleteBranch(String id) {
        branchRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found!"));

        branchRepository.deleteById(UUID.fromString(id));
    }

    public Branch findNearestBranch(double userLat, double userLon) {
        List<Branch> branches = branchRepository.findAll();

        return branches.stream()
                .filter(branch -> branch.getMarketing() != null && !branch.getMarketing().isEmpty())
                .min(Comparator.comparingDouble(branch -> locationCheck.countDistance(userLat, userLon,
                        branch.getLatitude(), branch.getLongitude())))
                .orElseThrow(() -> new RuntimeException("No branches with available marketing found"));
    }

    public Branch assignBranchManager(String branchId, String email) {
        Branch branch = branchRepository.findById(UUID.fromString(branchId))
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found!"));

        User manager = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        branch.setBranchManager(manager);
        return branchRepository.save(branch);
    }

    public Branch assignMarketing(String branchId, List<String> emails) {
        Branch branch = branchRepository.findById(UUID.fromString(branchId))
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found!"));

        List<User> marketers = emails.stream()
                .map(email -> userRepository.findByEmailIgnoreCase(email)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email)))
                .collect(Collectors.toList());

        marketers.forEach(marketing -> marketing.setBranch(branch));
        userRepository.saveAll(marketers);
        
        branch.setMarketing(marketers);
        return branchRepository.save(branch);
    }

}
