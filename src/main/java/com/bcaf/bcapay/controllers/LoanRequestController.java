package com.bcaf.bcapay.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bcaf.bcapay.dto.LoanRequestDto;
import com.bcaf.bcapay.dto.ResponseDto;
import com.bcaf.bcapay.models.LoanRequest;
import com.bcaf.bcapay.services.LoanRequestService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1/loan-requests")
public class LoanRequestController {

    @Autowired
    private LoanRequestService loanRequestService;

    @Secured({ "FEATURE_CREATE_LOAN_REQUEST", "FEATURE_MANAGE_LOAN_REQUESTS" })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> createLoanRequest(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestPart("amount") String amountStr,
            @RequestPart("tenor") String tenorStr,
            @RequestPart("latitude") String latitudeStr,
            @RequestPart("longitude") String longitudeStr,
            @RequestPart(value = "refferal", required = false) String refferal) {

        double amount = Double.parseDouble(amountStr);
        int tenor = Integer.parseInt(tenorStr);
        double latitude = Double.parseDouble(latitudeStr);
        double longitude = Double.parseDouble(longitudeStr);

        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", amount);
        payload.put("tenor", tenor);
        payload.put("latitude", latitude);
        payload.put("longitude", longitude);
        payload.put("refferal", refferal);

        LoanRequestDto loanRequest = loanRequestService.createLoanRequest(payload, token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(201, "success", "Pengajuan berhasil dibuat menunggu persetujuan", loanRequest));
    }

    // @Secured({ "FEATURE_CREATE_LOAN_REQUEST", "FEATURE_MANAGE_LOAN_REQUESTS" })
    // @PostMapping(value = "/a", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<ResponseDto> createLoanRequest(
    //         @RequestHeader(value = "Authorization", required = false) String token,
    //         @RequestPart("amount") double amount,
    //         @RequestPart("tenor") int tenor,
    //         @RequestPart("latitude") double latitude,
    //         @RequestPart("longitude") double longitude,
    //         @RequestPart(value = "refferal", required = false) String refferal,
    //         @RequestPart("ktp_image") MultipartFile ktpImage) {

    //     Map<String, Object> payload = new HashMap<>();
    //     payload.put("amount", amount);
    //     payload.put("tenor", tenor);
    //     payload.put("latitude", latitude);
    //     payload.put("longitude", longitude);
    //     payload.put("refferal", refferal);
    //     payload.put("ktp_image", ktpImage);

    //     LoanRequestDto loanRequest = loanRequestService.createLoanRequest(payload, token);
    //     return ResponseEntity.status(HttpStatus.CREATED)
    //             .body(new ResponseDto(201, "success", "Loan request created", loanRequest));
    // }

    @Secured({ "FEATURE_GET_ALL_LOAN_REQUEST", "FEATURE_MANAGE_LOAN_REQUESTS" })
    @GetMapping
    public ResponseEntity<ResponseDto> getAllLoanRequests() {
        List<?> loanRequests = loanRequestService.getAllLoanRequests();
        return ResponseEntity
                .ok(new ResponseDto(200, "success", loanRequests.size() + " loan requests found", loanRequests));
    }

    @Secured({ "FEATURE_GET_ALL_LOAN_REQUEST_BY_EMAIL", "FEATURE_MANAGE_LOAN_REQUESTS" })
    @GetMapping("/by-email")
    public ResponseEntity<ResponseDto> getAllLoanRequestsByEmail() {
        List<?> loanRequests = loanRequestService.getAllLoanRequestsByEmail();
        return ResponseEntity
                .ok(new ResponseDto(200, "success", loanRequests.size() + " loan requests found", loanRequests));
    }

    @Secured({ "FEATURE_GET_LOAN_REQUEST_BY_ID", "FEATURE_MANAGE_LOAN_REQUESTS" })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getLoanRequestById(@PathVariable String id) {
        LoanRequest loanRequest = loanRequestService.getLoanRequestById(id);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request found", loanRequest));
    }

    @Secured({ "FEATURE_UPDATE_LOAN_REQUEST", "FEATURE_MANAGE_LOAN_REQUESTS" })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateLoanRequest(@PathVariable String id,
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequest updatedLoanRequest = loanRequestService.updateLoanRequest(id, payload, token);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request updated", updatedLoanRequest));
    }

    @Secured("FEATURE_GET_ALL_LOAN_REQUEST_REVIEW")
    @GetMapping("/reviews")
    public ResponseEntity<ResponseDto> getAllLoanRequestReview() {
        List<?> loanRequests = loanRequestService.getAllLoanRequestReview();
        return ResponseEntity
                .ok(new ResponseDto(200, "success", loanRequests.size() + " loan requests found", loanRequests));
    }

    @Secured("FEATURE_GET_LOAN_REQUEST_BY_ID_REVIEW")
    @GetMapping("/reviews/{id}")
    public ResponseEntity<ResponseDto> getLoanRequestByIdReview(@PathVariable String id) {
        // Mengambil data dari service
        Map<String, Object> data = loanRequestService.getLoanRequestByIdReview(id);

        // Mengembalikan response dengan status 200 OK
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan requests found", data));
    }

    @Secured("FEATURE_UPDATE_LOAN_REQUEST_REVIEW")
    @PutMapping("/reviews/{id}")
    public ResponseEntity<ResponseDto> updateLoanRequestReview(@PathVariable String id,
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequestDto updatedLoanRequest = loanRequestService.updateLoanRequestReview(id, payload, token);
        boolean review = Boolean.parseBoolean(payload.get("review").toString());
        return ResponseEntity.ok(new ResponseDto(200, "success",
                "Berhasil " + (review ? "merekomendasikan" : "menolak") + " pengajuan", updatedLoanRequest));
    }

    @Secured("FEATURE_GET_ALL_LOAN_REQUEST_APPROVAL")
    @GetMapping("/approvals")
    public ResponseEntity<ResponseDto> getAllLoanRequestApproval() {
        List<?> loanRequests = loanRequestService.getAllLoanRequestApproval();
        return ResponseEntity
                .ok(new ResponseDto(200, "success", loanRequests.size() + " loan requests found", loanRequests));
    }

    @Secured("FEATURE_GET_LOAN_REQUEST_BY_ID_APPROVAL")
    @GetMapping("/approvals/{id}")
    public ResponseEntity<ResponseDto> getLoanRequestByIdApproval(@PathVariable String id) {
        // Mengambil data dari service
        Map<String, Object> data = loanRequestService.getLoanRequestByIdApproval(id);

        // Mengembalikan response dengan status 200 OK
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan requests found", data));
    }

    @Secured("FEATURE_UPDATE_LOAN_REQUEST_APPROVAL")
    @PutMapping("/approvals/{id}")
    public ResponseEntity<ResponseDto> updateLoanRequestApproval(@PathVariable String id,
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequestDto updatedLoanRequest = loanRequestService.updateLoanRequestApproval(id, payload, token);
        boolean approval = Boolean.parseBoolean(payload.get("approval").toString());
        return ResponseEntity.ok(new ResponseDto(200, "success",
                "Berhasil " + (approval ? "menyetujui" : "menolak") + " pengajuan", updatedLoanRequest));
    }

    @Secured("FEATURE_GET_LOAN_REQUEST_DISBURSEMENT")
    @GetMapping("/disbursement")
    public ResponseEntity<ResponseDto> backOfficeTakeOldestRequest(
            @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequestDto loanRequest = loanRequestService.backOfficeTakeOldestRequest(token);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request found", loanRequest));
    }

    @Secured("FEATURE_GET_LOAN_REQUEST_BY_ID_DISBURSEMENT")
    @GetMapping("/disbursement/{id}")
    public ResponseEntity<ResponseDto> getLoanRequestByIdDisbursement(@PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> data = loanRequestService.getLoanRequestByIdDisbursement(id);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request found", data));
    }

    @Secured("FEATURE_GET_LOAN_REQUEST_DISBURSEMENT")
    @GetMapping("/disbursement-count")
    public ResponseEntity<ResponseDto> getWaitingDisbursementCount() {
        long count = loanRequestService.countWaitingDisbursementRequests();
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request found", count));
    }

    @Secured("FEATURE_GET_ALL_LOAN_REQUEST_DISBURSEMENT_ONGOING")
    @GetMapping("/disbursement-ongoing")
    public ResponseEntity<ResponseDto> getAllLoanRequestDisbursementOngoing() {
        List<?> loanRequests = loanRequestService.getAllLoanRequestDisbursementOngoing();
        return ResponseEntity
                .ok(new ResponseDto(200, "success", loanRequests.size() + " loan requests found", loanRequests));
    }

    @Secured("FEATURE_UPDATE_LOAN_REQUEST_DISBURSEMENT")
    @PutMapping("/disbursement/{id}")
    public ResponseEntity<ResponseDto> updateLoanRequestDisbursement(@PathVariable String id,
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequestDto updatedLoanRequest = loanRequestService.updateLoanRequestDisbursement(id, payload, token);
        boolean disbursement = Boolean.parseBoolean(payload.get("disbursement").toString());
        return ResponseEntity.ok(new ResponseDto(200, "success",
                "Berhasil " + (disbursement ? "mencairkan" : "membatalkan pencairan") + " dana", updatedLoanRequest));
    }

    // @Secured("FEATURE_UPDATE_LOAN_REQUEST_DISBURSEMENT")
    // @PutMapping("/disbursement/{id}")
    // public ResponseEntity<ResponseDto> backOfficeDisbursement(@PathVariable
    // String id,
    // @RequestBody Map<String, Object> payload,
    // @RequestHeader(value = "Authorization", required = false) String token) {
    // LoanRequestDto updatedLoanRequest =
    // loanRequestService.backOfficeDisbursement(id, payload, token);
    // boolean disbursement =
    // Boolean.parseBoolean(payload.get("disbursement").toString());
    // return ResponseEntity.ok(new ResponseDto(200, "success",
    // "Berhasil " + (disbursement ? "mencairkan" : "menolak") + " pengajuan",
    // updatedLoanRequest));
    // }

    @Secured("FEATURE_ASSIGN_MARKETING")
    @PostMapping("/assign-marketing")
    public ResponseEntity<ResponseDto> assignMarketing(@RequestBody Map<String, Object> payload) {
        // LoanRequest updatedLoanRequest =
        String marketingEmail = payload.get("marketing_email").toString();
        LoanRequest data = loanRequestService.assignNonRefferalRequestToMarketing(payload);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request assigned to " + marketingEmail, data));
    }

    @Secured("FEATURE_BRANCH_MANAGER_LOAN_ACTION")
    @PutMapping("/branch-manager-action/{id}")
    public ResponseEntity<ResponseDto> branchManagerAction(@PathVariable String id,
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String token) {
        LoanRequestDto updatedLoanRequest = loanRequestService.branchManagerAction(id, payload, token);
        boolean approval = Boolean.parseBoolean(payload.get("branch_manager_approval").toString());
        return ResponseEntity.ok(new ResponseDto(200, "success",
                "Loan request " + (approval ? "Approved" : "Rejected") + " by Branch Manager", updatedLoanRequest));
    }

    @Secured("FEATURE_MANAGE_LOAN_REQUESTS")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteLoanRequest(@PathVariable String id) {
        loanRequestService.deleteLoanRequest(id);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Loan request deleted", null));
    }
}
