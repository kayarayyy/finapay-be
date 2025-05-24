package com.bcaf.finapay.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bcaf.finapay.models.Branch;
import com.bcaf.finapay.models.LoanRequest;
import com.bcaf.finapay.models.User;
import com.bcaf.finapay.models.enums.LoanStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lr FROM LoanRequest lr WHERE lr.id = :id")
    Optional<LoanRequest> findWithLockById(@Param("id") UUID id);

    List<LoanRequest> findByBranch(Branch branch);

    List<LoanRequest> findByCustomerEmail(String email);

    List<LoanRequest> findByCustomerEmailAndStatus(String email, LoanStatus status);

    List<LoanRequest> findByMarketingEmail(String marketing);

    List<LoanRequest> findByMarketingEmailAndMarketingApproveIsNull(String marketing);

    List<LoanRequest> findByMarketingIsNotNullAndMarketingApproveTrueAndBranchManagerApproveIsNullAndBranchManagerEmail(
            String branchManagerEmail);

    Optional<LoanRequest> findFirstByMarketingApproveTrueAndBranchManagerApproveTrueAndBackOfficeIsNullOrderByCreatedAtAsc();

    long countByMarketingApproveTrueAndBranchManagerApproveTrueAndBackOfficeIsNull();

    List<LoanRequest> findByBackOfficeEmailAndBackOfficeApproveDisburseIsNull(String email);

    @Query("SELECT COUNT(lr) FROM LoanRequest lr " +
            "WHERE lr.customer.email = :email " +
            "AND (lr.marketingApprove IS NULL OR lr.marketingApprove = true) " +
            "AND (lr.branchManagerApprove IS NULL OR lr.branchManagerApprove = true) " +
            "AND (lr.backOfficeApproveDisburse IS NULL OR lr.backOfficeApproveDisburse = true) " +
            "AND (" +
            "     lr.marketingApprove IS NULL " +
            "  OR lr.branchManagerApprove IS NULL " +
            "  OR lr.backOfficeApproveDisburse IS NULL" +
            ")")
    long countActiveLoanRequestsByCustomerEmail(@Param("email") String email);

}
