package com.bcaf.bcapay.dto;

import java.util.UUID;

import com.bcaf.bcapay.models.Branch;
import com.bcaf.bcapay.models.LoanRequest;

public class LoanRequestDto {
    private UUID id;
    private double amount;
    private String refferal;
    private UserDto customer;
    private UserDto marketing;
    private Boolean marketingApprove;
    private UserDto branchManager;
    private Boolean branchManagerApprove;
    private UserDto backOffice;
    private Boolean backOfficeApprove;
    private Double latitude;
    private Double longitude;
    private String branch;

    public LoanRequestDto(UUID id, double amount, String refferal, UserDto customer, UserDto marketing,
                          Boolean marketingApprove, UserDto branchManager, Boolean branchManagerApprove,
                          UserDto backOffice, Boolean backOfficeApprove,
                          Double latitude, Double longitude, Branch branch) {
        this.id = id;
        this.amount = amount;
        this.refferal = refferal;
        this.customer = customer;
        this.marketing = marketing;
        this.marketingApprove = marketingApprove;
        this.branchManager = branchManager;
        this.branchManagerApprove = branchManagerApprove;
        this.backOffice = backOffice;
        this.backOfficeApprove = backOfficeApprove;
        this.latitude = latitude;
        this.longitude = longitude;
        this.branch = branch.getName();
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }
    public String getRefferal() {
        return refferal;
    }

    public UserDto getCustomer() {
        return customer;
    }

    public UserDto getMarketing() {
        return marketing;
    }

    public Boolean getMarketingApprove() {
        return marketingApprove;
    }

    public UserDto getBranchManager() {
        return branchManager;
    }

    public Boolean getBranchManagerApprove() {
        return branchManagerApprove;
    }

    public UserDto getBackOffice() {
        return backOffice;
    }

    public Boolean getBackOfficeApprove() {
        return backOfficeApprove;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getBranch(){
        return branch;
    }

    // Convert from LoanRequest entity to DTO
    public static LoanRequestDto fromEntity(LoanRequest loanRequest) {
        return new LoanRequestDto(
            loanRequest.getId(),
            loanRequest.getAmount(),
            loanRequest.getRefferal(),
            loanRequest.getCustomer() != null ? UserDto.fromEntity(loanRequest.getCustomer()) : null,
            loanRequest.getMarketing() != null ? UserDto.fromEntity(loanRequest.getMarketing()) : null,
            loanRequest.getMarketingApprove(),
            loanRequest.getBranchManager() != null ? UserDto.fromEntity(loanRequest.getBranchManager()) : null,
            loanRequest.getBranchManagerApprove(),
            loanRequest.getBackOffice() != null ? UserDto.fromEntity(loanRequest.getBackOffice()) : null,
            loanRequest.getBackOfficeApproveDisburse(),
            loanRequest.getLatitude(),
            loanRequest.getLongitude(),
            loanRequest.getBranch()
        );
    }
}
