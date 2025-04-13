package com.bcaf.bcapay.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bcaf.bcapay.dto.LoanRequestDto;
import com.bcaf.bcapay.exceptions.ResourceNotFoundException;
import com.bcaf.bcapay.models.Branch;
import com.bcaf.bcapay.models.CustomerDetails;
import com.bcaf.bcapay.models.LoanRequest;
import com.bcaf.bcapay.models.User;
import com.bcaf.bcapay.repositories.LoanRequestRepository;
import com.bcaf.bcapay.utils.CurrencyUtil;
import com.bcaf.bcapay.utils.JwtUtil;
import com.bcaf.bcapay.utils.LoanUtil;
import com.bcaf.bcapay.utils.LocationCheck;

import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

@Service
public class LoanRequestService {

    @Autowired
    private LoanRequestRepository loanRequestRepository;

    @Autowired
    private BranchService branchService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LocationCheck locationCheck;

    @Autowired
    private CurrencyUtil currencyUtil;

    @Autowired
    private LoanUtil loanUtil;

    public LoanRequestDto createLoanRequest(Map<String, Object> payload, String token) {
        String email = jwtUtil.extractEmail(token);
        long activeRequestCount = loanRequestRepository.countActiveLoanRequestsByCustomerEmail(email);
        if (activeRequestCount > 0) {
            throw new IllegalArgumentException("You still have an ongoing loan request being processed.");
        }

        LoanRequest loanRequest = new LoanRequest();

        double amount = Double.parseDouble(payload.get("amount").toString());
        int tenor = Integer.parseInt(payload.get("tenor").toString());

        double userLat = Double.parseDouble(payload.get("latitude").toString());
        double userLon = Double.parseDouble(payload.get("longitude").toString());

        // Set Customer (required)
        CustomerDetails customerDetails = customerDetailsService.getByEmail(email);
        User customer = customerDetails.getUser();
        double availablePlafond = customerDetails.getAvailablePlafond();
        if (amount < 50000) {
            throw new IllegalArgumentException("The minimum allowed amount is Rp50.000.");
        }
        if (amount > availablePlafond) {
            throw new IllegalArgumentException("Your remaining plafond is " + currencyUtil.toRupiah(availablePlafond) +
                    ". You cannot request an amount greater than your available plafond.");
        }
        if (tenor < 1 || tenor > 12) {
            throw new IllegalArgumentException("Tenor exceeds the maximum limit of 12 and the minimum is 1 monthTenor exceeds the limit ma");
        }
        double annualRate = customerDetails.getPlafondPlan().getAnnualRate();
        double interest = loanUtil.calculateTotalInterest(amount, annualRate, tenor);

        loanRequest.setAmount(amount);
        loanRequest.setInterest(interest);
        loanRequest.setCustomer(customer);
        loanRequest.setLatitude(userLat);
        loanRequest.setLongitude(userLon);

        // Set Marketing (optional)
        if (payload.containsKey("refferal") && payload.get("refferal") != null) {
            User marketer = userService.getUserByRefferal(payload.get("refferal").toString());
            loanRequest.setMarketing(marketer);
            Branch branch = marketer.getBranch();
            loanRequest.setBranch(branch);
            loanRequest.setRefferal(payload.get("refferal").toString());
        } else {
            if (locationCheck.isOutsideIndonesia(userLat, userLon)) {
                // userLat = customerDetails.getLatitude();
                // userLon = customerDetails.getLongitude();
            }
            Branch nearestBranch = branchService.findNearestBranch(userLat, userLon);
            loanRequest.setBranch(nearestBranch);
            loanRequest.setBranchManager(nearestBranch.getBranchManager());
        }

        // LoanRequest savedLoanRequest = loanRequestRepository.save(loanRequest);
        // return LoanRequestDto.fromEntity(savedLoanRequest);
        return LoanRequestDto.fromEntity(loanRequest);
    }

    public List<LoanRequestDto> getAllLoanRequests() {
        return loanRequestRepository.findAll()
                .stream()
                .map(LoanRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    public LoanRequest getLoanRequestById(String id) {
        return loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));
    }

    public LoanRequest assignNonRefferalRequestToMarketing(Map<String, Object> payload) {
        String loanRequestId = payload.get("loan_request_id").toString();
        String marketingEmail = payload.get("marketing_email").toString();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailBm = null;

        if (authentication.getName() != null && authentication.getName() != "") {
            emailBm = authentication.getName();
        } else {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }
        LoanRequest loanRequest = getLoanRequestById(loanRequestId);
        Branch branch = loanRequest.getBranch();
        User branchManager = branch.getBranchManager();
        if (!branchManager.getEmail().equalsIgnoreCase(emailBm)) {
            throw new AccessDeniedException("You don't have permission to this resource");
        }
        if (loanRequest.getMarketing() != null) {
            throw new IllegalArgumentException(
                    "Marketing already assigned to " + loanRequest.getMarketing().getEmail());
        }
        User marketing = branch.getMarketing().stream()
                .filter(user -> marketingEmail.equals(user.getEmail()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Can't find marketing with: " + marketingEmail + " in your branch"));

        loanRequest.setMarketing(marketing);
        loanRequestRepository.save(loanRequest);

        return loanRequest;
    }

    public LoanRequestDto marketingAction(String id, Map<String, Object> payload, String token) {
        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (payload.containsKey("marketing_approval")) {
            String emailFromToken = jwtUtil.extractEmail(token);

            User marketing = loanRequest.getMarketing();
            if (marketing != null && emailFromToken.equalsIgnoreCase(marketing.getEmail())) {
                Boolean approval = Boolean.parseBoolean(payload.get("marketing_approval").toString());
                if (approval) {
                    loanRequest.setBranchManager(loanRequest.getBranch().getBranchManager());
                }
                loanRequest.setMarketingApprove(approval);
            } else {
                throw new AccessDeniedException("You are not authorized to approve or reject as Marketing.");
            }
        }
        LoanRequest savedLoanRequest = loanRequestRepository.save(loanRequest);
        return LoanRequestDto.fromEntity(savedLoanRequest);
    }

    public LoanRequestDto branchManagerAction(String id, Map<String, Object> payload, String token) {
        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (loanRequest.getMarketingApprove() == null) {
            throw new IllegalArgumentException("Contact your Marketing to Approve first");
        } else if (!loanRequest.getMarketingApprove()) {
            throw new IllegalArgumentException("Already Rejected by Marketing");
        }

        if (payload.containsKey("branch_manager_approval")) {
            String emailFromToken = jwtUtil.extractEmail(token);

            User marketing = loanRequest.getBranchManager();
            if (marketing != null && emailFromToken.equalsIgnoreCase(marketing.getEmail())) {
                loanRequest.setBranchManagerApprove(
                        Boolean.parseBoolean(payload.get("branch_manager_approval").toString()));
            } else {
                throw new AccessDeniedException("You are not authorized to approve or reject as Branch Manager.");
            }
        }
        LoanRequest savedLoanRequest = loanRequestRepository.save(loanRequest);
        return LoanRequestDto.fromEntity(savedLoanRequest);
    }

    @Transactional
    public LoanRequestDto backOfficeProceed(String id, String token) {
        LoanRequest loanRequest = loanRequestRepository.findWithLockById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (loanRequest.getMarketingApprove() == null) {
            throw new IllegalArgumentException("Contact your Marketing to Approve first");
        } else if (!loanRequest.getMarketingApprove()) {
            throw new IllegalArgumentException("Already Rejected by Marketing");
        }
        if (loanRequest.getBranchManagerApprove() == null) {
            throw new IllegalArgumentException("Contact your Branch Manager to Approve first");
        } else if (!loanRequest.getBranchManagerApprove()) {
            throw new IllegalArgumentException("Already Rejected by Branch Manager");
        }
        if (loanRequest.getBackOffice() == null) {
            String email = jwtUtil.extractEmail(token);
            User backOffice = userService.getUserByEmail(email);
            loanRequest.setBackOffice(backOffice);
            LoanRequest savedLoanRequest = loanRequestRepository.save(loanRequest);
            return LoanRequestDto.fromEntity(savedLoanRequest);
        } else {
            throw new IllegalArgumentException("Request already taken by other Officer");
        }

    }

    public LoanRequestDto backOfficeDisbursement(String id, Map<String, Object> payload, String token) {
        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (Boolean.TRUE.equals(loanRequest.getBackOfficeApproveDisburse())) {
            throw new IllegalArgumentException("Request already disbursed");
        } else if (Boolean.FALSE.equals(loanRequest.getBackOfficeApproveDisburse())) {
            throw new IllegalArgumentException("Disbursement request already rejected");
        }

        if (payload.containsKey("back_office_approval_disbursement")) {
            String emailFromToken = jwtUtil.extractEmail(token);

            User backOffice = loanRequest.getBackOffice();
            if (backOffice != null && emailFromToken.equalsIgnoreCase(backOffice.getEmail())) {
                Boolean approval = Boolean.parseBoolean(payload.get("back_office_approval_disbursement").toString());
                if (approval) {
                    CustomerDetails customerDetails = customerDetailsService
                            .getByEmail(loanRequest.getCustomer().getEmail());
                    double availablePlafond = customerDetails.getAvailablePlafond() - loanRequest.getAmount() - loanRequest.getInterest();
                    customerDetails.setAvailablePlafond(availablePlafond);
                    customerDetailsService.update(customerDetails.getId(), customerDetails);
                }
                loanRequest.setBackOfficeApproveDisburse(approval);
                loanRequest.setCompletedAt(LocalDateTime.now());
            } else {
                throw new AccessDeniedException("You are not authorized to disburse loan as Back Office.");
            }
        }
        LoanRequest savedLoanRequest = loanRequestRepository.save(loanRequest);
        return LoanRequestDto.fromEntity(savedLoanRequest);
    }

    public LoanRequest updateLoanRequest(String id, Map<String, Object> payload, String token) {
        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (payload.containsKey("amount")) {
            double amount = Double.parseDouble(payload.get("amount").toString());
            loanRequest.setAmount(amount);
        }

        if (payload.containsKey("marketing_approval")) {
            loanRequest.setMarketingApprove(Boolean.parseBoolean(payload.get("marketing_approval").toString()));
        }

        if (payload.containsKey("branchManagerApprove")) {
            loanRequest.setBranchManagerApprove(Boolean.parseBoolean(payload.get("branchManagerApprove").toString()));
        }

        if (payload.containsKey("backOfficeApprove")) {
            loanRequest.setBackOfficeApproveDisburse(Boolean.parseBoolean(payload.get("backOfficeApprove").toString()));
        }

        // return loanRequestRepository.save(loanRequest);
        return loanRequest;
    }

    public void deleteLoanRequest(String id) {
        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        loanRequestRepository.delete(loanRequest);
    }
}
