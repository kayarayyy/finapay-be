package com.bcaf.finapay.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bcaf.finapay.models.Branch;
import com.bcaf.finapay.models.LoanRequest;
import com.bcaf.finapay.models.enums.LoanStatus;
import com.bcaf.finapay.utils.CurrencyUtil;
import com.bcaf.finapay.utils.DateFormatterUtil;

public class LoanRequestDto {
    private UUID id;
    private String amount;
    private int tenor;
    private Double interest;
    private Double adminFee;
    private String refferal;
    private String purpose;
    private UserDto customer;
    private UserDto marketing;
    private Boolean marketingApprove;
    private String marketingNotes;
    private String marketingReviewedAt;
    private UserDto branchManager;
    private Boolean branchManagerApprove;
    private String branchManagerNotes;
    private String branchManagerApprovedAt;
    private UserDto backOffice;
    private Boolean backOfficeApprove;
    private String backOfficeNotes;
    private String backOfficeDisbursedAt;
    private Double latitude;
    private Double longitude;
    private String branch;
    private LoanStatus status;
    private String instalment;

    public LoanRequestDto(UUID id, double amount, int tenor, Double interest, Double adminFee, String refferal, String purpose,
            UserDto customer, UserDto marketing,
            Boolean marketingApprove, String marketingNotes, LocalDateTime marketingReviewedAt, UserDto branchManager,
            Boolean branchManagerApprove, String branchManagerNotes, LocalDateTime branchManagerApprovedAt,
            UserDto backOffice, Boolean backOfficeApprove, String backOfficeNotes, LocalDateTime backOfficeDisbursedAt,
            Double latitude, Double longitude, Branch branch, LoanStatus status) {
        this.id = id;
        this.amount = CurrencyUtil.toRupiah(amount);
        this.tenor = tenor;
        this.interest = interest;
        this.adminFee = adminFee;
        this.refferal = refferal;
        this.purpose = purpose;
        this.customer = customer;
        this.marketing = marketing;
        this.marketingApprove = marketingApprove;
        this.marketingNotes = marketingNotes;
        this.marketingReviewedAt = marketingReviewedAt != null
                ? DateFormatterUtil.formatToIndonesianDate(marketingReviewedAt)
                : null;
        this.branchManager = branchManager;
        this.branchManagerApprove = branchManagerApprove;
        this.branchManagerNotes = branchManagerNotes;
        this.branchManagerApprovedAt = branchManagerApprovedAt != null
                ? DateFormatterUtil.formatToIndonesianDate(branchManagerApprovedAt)
                : null;
        this.backOffice = backOffice;
        this.backOfficeApprove = backOfficeApprove;
        this.backOfficeNotes = backOfficeNotes;
        this.backOfficeDisbursedAt = backOfficeDisbursedAt != null
                ? DateFormatterUtil.formatToIndonesianDate(backOfficeDisbursedAt)
                : null;
        this.latitude = latitude;
        this.longitude = longitude;
        this.branch = branch.getName();
        this.status = status;
        this.instalment = CurrencyUtil.toRupiah((amount + interest + adminFee) / tenor);
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public int getTenor() {
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
    public String getPurpose() {
        return purpose;
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

    public String getMarketingReviewedAt() {
        return marketingReviewedAt;
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

    public String getBranchManagerApprovedAt() {
        return branchManagerApprovedAt;
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

    public String getBackOfficeDisbursedAt() {
        return backOfficeDisbursedAt;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getBranch() {
        return branch;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public String getInstalment() {
        return instalment;
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
                loanRequest.getPurpose(),
                loanRequest.getCustomer() != null ? UserDto.fromEntity(loanRequest.getCustomer()) : null,
                loanRequest.getMarketing() != null ? UserDto.fromEntity(loanRequest.getMarketing()) : null,
                loanRequest.getMarketingApprove(),
                loanRequest.getMarketingNotes(),
                loanRequest.getMarketingReviewedAt(),
                loanRequest.getBranchManager() != null ? UserDto.fromEntity(loanRequest.getBranchManager()) : null,
                loanRequest.getBranchManagerApprove(),
                loanRequest.getBranchManagerNotes(),
                loanRequest.getBranchManagerApprovedAt(),
                loanRequest.getBackOffice() != null ? UserDto.fromEntity(loanRequest.getBackOffice()) : null,
                loanRequest.getBackOfficeApproveDisburse(),
                loanRequest.getBackOfficeNotes(),
                loanRequest.getBackOfficeDisbursedAt(),
                loanRequest.getLatitude(),
                loanRequest.getLongitude(),
                loanRequest.getBranch(),
                loanRequest.getStatus());
    }
}
