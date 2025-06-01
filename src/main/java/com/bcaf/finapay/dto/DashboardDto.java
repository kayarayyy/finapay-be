package com.bcaf.finapay.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.bcaf.finapay.models.Branch;
import com.bcaf.finapay.models.LoanRequest;
import com.bcaf.finapay.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DashboardDto {
    private int totalBranches;
    private int totalActiveUsers;
    private int totalLoanRequests;
    private int totalApproved;
    private int totalRejected;
    private int totalPending;
    private BigDecimal amountApproved;
    private BigDecimal amountRejected;
    private BigDecimal amountPending;
    private List<BranchDto> branches;

    public DashboardDto(
            int totalBranches,
            int totalActiveUsers,
            int totalLoanRequests,
            int totalApproved,
            int totalRejected,
            int totalPending,
            BigDecimal amountApproved,
            BigDecimal amountRejected,
            BigDecimal amountPending,
            List<BranchDto> branches) {
        this.totalBranches = totalBranches;
        this.totalActiveUsers = totalActiveUsers;
        this.totalLoanRequests = totalLoanRequests;
        this.totalApproved = totalApproved;
        this.totalRejected = totalRejected;
        this.totalPending = totalPending;
        this.amountApproved = amountApproved;
        this.amountRejected = amountRejected;
        this.amountPending = amountPending;
        this.branches = branches;
    }

    public static DashboardDto fromEntity(List<User> users, List<Branch> branches, List<LoanRequest> loanRequests) {
        int totalBranches = branches.size();
        int totalActiveUsers = (int) users.stream().filter(User::isActive).count();

        int totalLoanRequests = loanRequests.size();
        int totalApproved = 0, totalRejected = 0, totalPending = 0;
        BigDecimal amountApproved = BigDecimal.ZERO;
        BigDecimal amountRejected = BigDecimal.ZERO;
        BigDecimal amountPending = BigDecimal.ZERO;

        for (LoanRequest lr : loanRequests) {
            String status = lr.getStatus().toString();
            BigDecimal amount = lr.getAmount() != null
                    ? BigDecimal.valueOf(lr.getAmount())
                    : BigDecimal.ZERO;

            if ("approved".equalsIgnoreCase(status)) {
                totalApproved++;
                amountApproved = amountApproved.add(amount);
            } else if ("rejected".equalsIgnoreCase(status)) {
                totalRejected++;
                amountRejected = amountRejected.add(amount);
            } else {
                totalPending++;
                amountPending = amountPending.add(amount);
            }
        }

        List<BranchDto> branchDtos = branches.stream()
                .map(BranchDto::fromEntity)
                .collect(Collectors.toList());

        return new DashboardDto(
                totalBranches,
                totalActiveUsers,
                totalLoanRequests,
                totalApproved,
                totalRejected,
                totalPending,
                amountApproved,
                amountRejected,
                amountPending,
                branchDtos);
    }

    // Getter methods

    public int getTotalBranches() {
        return totalBranches;
    }

    public int getTotalActiveUsers() {
        return totalActiveUsers;
    }

    public int getTotalLoanRequests() {
        return totalLoanRequests;
    }

    public int getTotalApproved() {
        return totalApproved;
    }

    public int getTotalRejected() {
        return totalRejected;
    }

    public int getTotalPending() {
        return totalPending;
    }

    public BigDecimal getAmountApproved() {
        return amountApproved;
    }

    public BigDecimal getAmountRejected() {
        return amountRejected;
    }

    public BigDecimal getAmountPending() {
        return amountPending;
    }

    public List<BranchDto> getBranches() {
        return branches;
    }
}
