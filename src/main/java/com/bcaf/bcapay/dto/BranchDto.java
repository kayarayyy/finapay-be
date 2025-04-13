package com.bcaf.bcapay.dto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.bcaf.bcapay.models.Branch;

public class BranchDto {
    private UUID id;
    private String name;
    private String city;
    private double latitude;
    private double longitude;
    private UserDto branchManager;
    private List<UserDto> marketing;

    public BranchDto(UUID id, String name, String city, double latitude, double longitude,
                     UserDto branchManager, List<UserDto> marketing) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.branchManager = branchManager;
        this.marketing = marketing;
    }

    // Getter
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public UserDto getBranchManager() { return branchManager; }
    public List<UserDto> getMarketing() { return marketing; }

    public static BranchDto fromEntity(Branch branch) {
        return new BranchDto(
            branch.getId(),
            branch.getName(),
            branch.getCity().name(),
            branch.getLatitude(),
            branch.getLongitude(),
            branch.getBranchManager() != null ? UserDto.fromEntity(branch.getBranchManager()) : null,
            branch.getMarketing().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList())
        );
    }
}
