package com.bcaf.bcapay.dto;

import java.util.UUID;

import com.bcaf.bcapay.models.Branch;
import com.bcaf.bcapay.models.LoanRequest;
import com.bcaf.bcapay.models.enums.LoanStatus;
import com.bcaf.bcapay.utils.CurrencyUtil;

public class LoanRequestDto {
    private UUID id;
    private String amount;
    private int tenor;
    private Double interest;
    private Double adminFee;
    private String refferal;
    private UserDto customer;
    private UserDto marketing;
    private Boolean marketingApprove;
    private String marketingNotes;
    private UserDto branchManager;
    private Boolean branchManagerApprove;
    private String branchManagerNotes;
    private UserDto backOffice;
    private Boolean backOfficeApprove;
    private String backOfficeNotes;
    private Double latitude;
    private Double longitude;
    private String branch;
    private LoanStatus status;

    public LoanRequestDto(UUID id, double amount, int tenor, Double interest, Double adminFee, String refferal, UserDto customer, UserDto marketing,
                          Boolean marketingApprove, String marketingNotes, UserDto branchManager, Boolean branchManagerApprove,String branchManagerNotes,
                          UserDto backOffice, Boolean backOfficeApprove, String backOfficeNotes,
                          Double latitude, Double longitude, Branch branch, LoanStatus status) {
        this.id = id;
        this.amount = CurrencyUtil.toRupiah(amount);
        this.tenor = tenor;
        this.interest = interest;
        this.adminFee = adminFee;
        this.refferal = refferal;
        this.customer = customer;
        this.marketing = marketing;
        this.marketingApprove = marketingApprove;
        this.marketingNotes = marketingNotes;
        this.branchManager = branchManager;
        this.branchManagerApprove = branchManagerApprove;
        this.branchManagerNotes = branchManagerNotes;
        this.backOffice = backOffice;
        this.backOfficeApprove = backOfficeApprove;
        this.backOfficeNotes = backOfficeNotes;
        this.latitude = latitude;
        this.longitude = longitude;
        this.branch = branch.getName();
        this.status = status;
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }
    public int getTenor(){
        return tenor;
    }
    public Double getInterest() {
        return interest;
    }
    public Double getAdminFee() {
        return adminFee;
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
    public String getMarketingNotes() {
        return marketingNotes;
    }
    
    public UserDto getBranchManager() {
        return branchManager;
    }
    
    public Boolean getBranchManagerApprove() {
        return branchManagerApprove;
    }
    public String getBranchManagerNotes() {
        return branchManagerNotes;
    }
    
    public UserDto getBackOffice() {
        return backOffice;
    }

    public Boolean getBackOfficeApprove() {
        return backOfficeApprove;
    }
    public String getBackOfficeNotes() {
        return backOfficeNotes;
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

    public LoanStatus getStatus(){
        return status;
    }

    // Convert from LoanRequest entity to DTO
    public static LoanRequestDto fromEntity(LoanRequest loanRequest) {
        return new LoanRequestDto(
            loanRequest.getId(),
            loanRequest.getAmount(),
            loanRequest.getTenor(),
            loanRequest.getInterest(),
            loanRequest.getAdminFee(),
            loanRequest.getRefferal(),
            loanRequest.getCustomer() != null ? UserDto.fromEntity(loanRequest.getCustomer()) : null,
            loanRequest.getMarketing() != null ? UserDto.fromEntity(loanRequest.getMarketing()) : null,
            loanRequest.getMarketingApprove(),
            loanRequest.getMarketingNotes(),
            loanRequest.getBranchManager() != null ? UserDto.fromEntity(loanRequest.getBranchManager()) : null,
            loanRequest.getBranchManagerApprove(),
            loanRequest.getBranchManagerNotes(),
            loanRequest.getBackOffice() != null ? UserDto.fromEntity(loanRequest.getBackOffice()) : null,
            loanRequest.getBackOfficeApproveDisburse(),
            loanRequest.getBackOfficeNotes(),
            loanRequest.getLatitude(),
            loanRequest.getLongitude(),
            loanRequest.getBranch(),
            loanRequest.getStatus()
        );
    }
}
