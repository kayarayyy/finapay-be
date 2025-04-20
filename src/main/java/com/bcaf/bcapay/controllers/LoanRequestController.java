package com.bcaf.bcapay.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.bcaf.bcapay.dto.LoanRequestDto;
import com.bcaf.bcapay.dto.ResponseDto;
import com.bcaf.bcapay.models.LoanRequest;
import com.bcaf.bcapay.services.LoanRequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/loan-requests")
public class LoanRequestController {

    @Autowired
    private LoanRequestService loanRequestService;

    @Secured({ "FEATURE_MANAGE_LOAN_REQUESTS", "FEATURE_APPLY_LOAN" })
    @PostMapping
    public ResponseEntity<ResponseDto> createLoanRequest(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> payload) {
        LoanRequestDto loanRequest = loanRequestService.createLoanRequest(payload, token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(201, "success", "Loan request created", loanRequest));
    }

    @Secured("FEATURE_MANAGE_LOAN_REQUESTS")
    @GetMapping
    public ResponseEntity<ResponseDto> getAllLoanRequests() {
        List<?> loanRequests = loanRequestService.getAllLoanRequests();
        return ResponseEntity
                .ok(new ResponseDto(200, "success", loanRequests.size() + " loan requests found", loanRequests));
    }

    @Secured("FEATURE_MARKETING_REVIEW")
    @GetMapping("/reviews")
    public ResponseEntity<ResponseDto> getMarketingLoanReview() {
        List<?> loanRequests = loanRequestService.getMarketingLoanReview();
        return ResponseEntity
                .ok(new ResponseDto(200, "success", loanRequests.size() + " loan requests found", loanRequests));
    }

    @Secured({"FEATURE_MANAGE_LOAN_REQUESTS", "FEATURE_ASSIGN_MARKETING"})
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getLoanRequestById(@PathVariable String id) {
        LoanRequest loanRequest = loanRequestService.getLoanRequestById(id);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request found", loanRequest));
    }

    @Secured("FEATURE_MANAGE_LOAN_REQUESTS")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateLoanRequest(@PathVariable String id, @RequestBody Map<String, Object> payload, @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequest updatedLoanRequest = loanRequestService.updateLoanRequest(id, payload, token);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request updated", updatedLoanRequest));
    }

    @Secured("FEATURE_ASSIGN_MARKETING")
    @PostMapping("/assign-marketing")
    public ResponseEntity<ResponseDto> assignMarketing(@RequestBody Map<String, Object> payload) {
        // LoanRequest updatedLoanRequest = 
        String marketingEmail = payload.get("marketing_email").toString();
        LoanRequest data = loanRequestService.assignNonRefferalRequestToMarketing(payload);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request assigned to " + marketingEmail, data));
    }

    @Secured("FEATURE_MARKETING_LOAN_ACTION")
    @PutMapping("/marketing-action/{id}")
    public ResponseEntity<ResponseDto> marketingAction(@PathVariable String id, @RequestBody Map<String, Object> payload, @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequestDto updatedLoanRequest = loanRequestService.marketingAction(id, payload, token);
        boolean approval = Boolean.parseBoolean(payload.get("marketing_approval").toString());
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request " + (approval ? "Approved" : "Rejected") + " by Marketing", updatedLoanRequest));
    }

    @Secured("FEATURE_BRANCH_MANAGER_LOAN_ACTION")
    @PutMapping("/branch-manager-action/{id}")
    public ResponseEntity<ResponseDto> branchManagerAction(@PathVariable String id, @RequestBody Map<String, Object> payload, @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequestDto updatedLoanRequest = loanRequestService.branchManagerAction(id, payload, token);
        boolean approval = Boolean.parseBoolean(payload.get("branch_manager_approval").toString());
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request " + (approval ? "Approved" : "Rejected") + " by Branch Manager", updatedLoanRequest));
    }

    @Secured("FEATURE_BACK_OFFICE_PROCEED")
    @GetMapping("/back-office-proceed/{id}")
    public ResponseEntity<ResponseDto> backOfficeProceed(@PathVariable String id, @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequestDto loanRequest = loanRequestService.backOfficeProceed(id, token);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request found", loanRequest));
    }

    @Secured("FEATURE_BACK_OFFICE_APPROVAL_DISBURSEMENT")
    @PutMapping("/back-office-approval-disbursement/{id}")
    public ResponseEntity<ResponseDto> backOfficeDisbursement(@PathVariable String id, @RequestBody Map<String, Object> payload, @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequestDto updatedLoanRequest = loanRequestService.backOfficeDisbursement(id, payload, token);
        boolean approval = Boolean.parseBoolean(payload.get("back_office_approval_disbursement").toString());
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request " + (approval ? "Approved" : "Rejected") + " Disbursement by Back Office", updatedLoanRequest));
    }

    @Secured("FEATURE_MANAGE_LOAN_REQUESTS")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteLoanRequest(@PathVariable String id) {
        loanRequestService.deleteLoanRequest(id);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request deleted", null));
    }
}
