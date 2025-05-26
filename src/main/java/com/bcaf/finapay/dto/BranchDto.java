package com.bcaf.finapay.dto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.bcaf.finapay.models.Branch;

public class BranchDto {
    private UUID id;
    private String name;
    private String city;
    private Double latitude;
    private Double longitude;
    private UserDto branchManager;
    private List<UserDto> marketing;

    public BranchDto(UUID id, String name, String city, Double latitude, Double longitude,
                     UserDto branchManager, List<UserDto> marketing) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.branchManager = branchManager;
        this.marketing = marketing;
    }

    // ✅ Gunakan tipe wrapper Double, bukan primitif double
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
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
            branch.getMarketing() != null ?
                branch.getMarketing().stream()
                    .map(UserDto::fromEntity)
                    .collect(Collectors.toList())
                : null
        );
    }
}
