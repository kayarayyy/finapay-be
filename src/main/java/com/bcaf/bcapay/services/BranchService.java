package com.bcaf.bcapay.services;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcaf.bcapay.dto.BranchDto;
import com.bcaf.bcapay.exceptions.ResourceNotFoundException;
import com.bcaf.bcapay.models.Branch;
import com.bcaf.bcapay.models.enums.City;
import com.bcaf.bcapay.repositories.BranchRepository;
import com.bcaf.bcapay.utils.LocationCheck;

@Service
public class BranchService {
    @Autowired
    private BranchRepository branchRepository;

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

        City city;
        try {
            city = City.valueOf(cityName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid city name: " + cityName);
        }

        Branch branch = new Branch();
        branch.setName(name);
        branch.setCity(city);

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
            .min(Comparator.comparingDouble(branch ->
                locationCheck.countDistance(userLat, userLon, branch.getLatitude(), branch.getLongitude())))
            .orElseThrow(() -> new RuntimeException("No branches available"));
    }
}
