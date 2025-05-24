package com.bcaf.bcapay.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bcaf.bcapay.dto.CustomerDetailsDto;
import com.bcaf.bcapay.dto.LoanRequestDto;
import com.bcaf.bcapay.exceptions.ResourceNotFoundException;
import com.bcaf.bcapay.models.Branch;
import com.bcaf.bcapay.models.CustomerDetails;
import com.bcaf.bcapay.models.FcmToken;
import com.bcaf.bcapay.models.LoanRequest;
import com.bcaf.bcapay.models.User;
import com.bcaf.bcapay.models.enums.LoanStatus;
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

    @Autowired
    private FCMService fcmService;

    @Autowired
    private FcmTokenServices fcmTokenServices;

    // @Autowired
    // private FileStorageService fileStorageService;

    public LoanRequestDto createLoanRequest(Map<String, Object> payload, String token) {
        String email = jwtUtil.extractEmail(token);
        long activeRequestCount = loanRequestRepository.countActiveLoanRequestsByCustomerEmail(email);
        if (activeRequestCount > 0) {
            throw new IllegalArgumentException("Masih ada pengajuan yang sedang berlangsung, silahkan menunggu terlebih dahulu");
        }

        LoanRequest loanRequest = new LoanRequest();

        double amount = (payload.containsKey("amount") && payload.get("amount") instanceof Number)
                ? ((Number) payload.get("amount")).doubleValue()
                : 0.0;

        int tenor = (payload.containsKey("tenor") && payload.get("tenor") instanceof Number)
                ? ((Number) payload.get("tenor")).intValue()
                : 0;

        double userLat = Double.parseDouble(payload.get("latitude").toString());
        double userLon = Double.parseDouble(payload.get("longitude").toString());

        // Set Customer (required)
        CustomerDetails customerDetails = customerDetailsService.getByEmail(email);
        User customer = customerDetails.getUser();
        double availablePlafond = customerDetails.getAvailablePlafond();
        if (amount < 50000) {
            throw new IllegalArgumentException("Pengajuan peminjaman minimal Rp50.000.");
        }
        if (amount > availablePlafond) {
            throw new IllegalArgumentException("Sisa plafond anda adalah " + currencyUtil.toRupiah(availablePlafond) +
                    ". Anda tidak dapat mengajukan lebih dari sisa plafond anda.");
        }
        if (tenor < 6 || tenor > 24) {
            throw new IllegalArgumentException(
                    "Tenor tidak memenuhi syarat, tenor minimal 6 dan maksimal 24");
        }
        // MultipartFile ktpImage = (MultipartFile) payload.get("ktpImage");
        // if (ktpImage == null || ktpImage.isEmpty()) {
        //     throw new IllegalArgumentException("KTP image is required.");
        // }

        double annualRate = customerDetails.getPlafondPlan().getAnnualRate();
        double adminRate = customerDetails.getPlafondPlan().getAdminRate();
        double interest = loanUtil.calculateTotalInterest(amount, annualRate, tenor);
        double adminFee = loanUtil.calculateTotalAdminFee(amount, adminRate);
        
        // Simpan ke storage (local/cloud), misalnya:
        // String ktpPath = fileStorageService.saveImage(ktpImage);
        // loanRequest.setKtpImagePath(ktpPath);

        loanRequest.setAmount(amount);
        loanRequest.setInterest(interest);
        loanRequest.setAdminFee(adminFee);
        loanRequest.setCustomer(customer);
        loanRequest.setLatitude(userLat);
        loanRequest.setLongitude(userLon);
        loanRequest.setTenor(tenor);
        loanRequest.setStatus(LoanStatus.REVIEW);

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

        LoanRequest savedLoanRequest = loanRequestRepository.save(loanRequest);
        return LoanRequestDto.fromEntity(savedLoanRequest);
        // return LoanRequestDto.fromEntity(loanRequest);
    }

    public List<LoanRequestDto> getAllLoanRequests() {
        return loanRequestRepository.findAll()
                .stream()
                .map(LoanRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<LoanRequestDto> getAllLoanRequestsByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailCustomer = null;
        if (authentication.getName() != null && authentication.getName() != "") {
            emailCustomer = authentication.getName();
        } else {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }
        return loanRequestRepository.findByCustomerEmail(emailCustomer)
                .stream()
                .map(LoanRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<LoanRequestDto> getAllLoanRequestReview() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailMarketing = null;
        if (authentication.getName() != null && authentication.getName() != "") {
            emailMarketing = authentication.getName();
        } else {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }
        return loanRequestRepository.findByMarketingEmailAndMarketingApproveIsNull(emailMarketing)
                .stream()
                .map(LoanRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getLoanRequestByIdReview(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailMarketing = authentication.getName();

        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (!emailMarketing.equalsIgnoreCase(loanRequest.getMarketing().getEmail())) {
            throw new AccessDeniedException("You are not authorized to get this loan request.");
        }

        CustomerDetails customerDetails = customerDetailsService.getByEmail(loanRequest.getCustomer().getEmail());

        LoanRequestDto loanRequestDto = LoanRequestDto.fromEntity(loanRequest);
        CustomerDetailsDto customerDetailsDto = CustomerDetailsDto.fromEntity(customerDetails);

        // Pastikan kamu memasukkan objek yang sesuai dalam map
        Map<String, Object> data = new HashMap<>();
        data.put("loanRequest", loanRequestDto);
        data.put("customerDetails", customerDetailsDto);

        return data;
    }

    public LoanRequestDto updateLoanRequestReview(String id, Map<String, Object> payload, String token) {
        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (payload.containsKey("review")) {
            String emailFromToken = jwtUtil.extractEmail(token);

            User marketing = loanRequest.getMarketing();
            if (marketing != null && emailFromToken.equalsIgnoreCase(marketing.getEmail())) {
                Boolean approval = Boolean.parseBoolean(payload.get("review").toString());
                if (approval) {
                    loanRequest.setStatus(LoanStatus.APPROVAL);
                    loanRequest.setBranchManager(loanRequest.getBranch().getBranchManager());
                } else {
                    loanRequest.setStatus(LoanStatus.REJECTED);
                }
                loanRequest.setMarketingApprove(approval);
            } else {
                throw new AccessDeniedException("You are not authorized to approve or reject as Marketing.");
            }
        }
        if (payload.containsKey("notes")) {
            String notes = payload.get("notes").toString();
            loanRequest.setMarketingNotes(notes);
        }
        LoanRequest savedLoanRequest = loanRequestRepository.save(loanRequest);
        return LoanRequestDto.fromEntity(savedLoanRequest);
    }

    public List<LoanRequestDto> getAllLoanRequestApproval() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailBranchManager = null;
        if (authentication.getName() != null && authentication.getName() != "") {
            emailBranchManager = authentication.getName();
        } else {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }
        return loanRequestRepository
                .findByMarketingIsNotNullAndMarketingApproveTrueAndBranchManagerApproveIsNullAndBranchManagerEmail(
                        emailBranchManager)
                .stream()
                .map(LoanRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getLoanRequestByIdApproval(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailBranchManager = authentication.getName();

        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (!emailBranchManager.equalsIgnoreCase(loanRequest.getBranchManager().getEmail())) {
            throw new AccessDeniedException("You are not authorized to get this loan request.");
        }

        CustomerDetails customerDetails = customerDetailsService.getByEmail(loanRequest.getCustomer().getEmail());

        LoanRequestDto loanRequestDto = LoanRequestDto.fromEntity(loanRequest);
        CustomerDetailsDto customerDetailsDto = CustomerDetailsDto.fromEntity(customerDetails);

        // Pastikan kamu memasukkan objek yang sesuai dalam map
        Map<String, Object> data = new HashMap<>();
        data.put("loanRequest", loanRequestDto);
        data.put("customerDetails", customerDetailsDto);

        return data;
    }

    public LoanRequestDto updateLoanRequestApproval(String id, Map<String, Object> payload, String token) {
        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (payload.containsKey("approval")) {
            String emailFromToken = jwtUtil.extractEmail(token);

            User branchManager = loanRequest.getBranchManager();
            if (branchManager != null && emailFromToken.equalsIgnoreCase(branchManager.getEmail())) {
                Boolean approval = Boolean.parseBoolean(payload.get("approval").toString());

                if (approval) {
                    loanRequest.setStatus(LoanStatus.DISBURSEMENT);
                    List<FcmToken> tokens = fcmTokenServices.getTokensByEmail(loanRequest.getCustomer().getEmail());

                    // if (tokens.isEmpty()) {
                    // throw new IllegalArgumentException(
                    // "FCM token tidak ditemukan untuk email: " +
                    // loanRequest.getCustomer().getEmail());
                    // }

                    for (FcmToken fcmToken : tokens) {
                        try {
                            fcmService.sendNotification(
                                    fcmToken.getToken(),
                                    "Pengajuan Disetujui - FINAPay",
                                    "Pengajuan telah disetujui menunggu pencairan dana");
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Notifikasi gagal terkirim");
                        }
                    }
                } else {
                    loanRequest.setStatus(LoanStatus.REJECTED);
                }

                loanRequest.setBranchManagerApprove(approval);
            } else {
                throw new AccessDeniedException("You are not authorized to approve or reject as Marketing.");
            }
        }
        if (payload.containsKey("notes")) {
            String notes = payload.get("notes").toString();
            loanRequest.setBranchManagerNotes(notes);
        }
        LoanRequest savedLoanRequest = loanRequestRepository.save(loanRequest);
        return LoanRequestDto.fromEntity(savedLoanRequest);
    }

    @Transactional
    public LoanRequestDto backOfficeTakeOldestRequest(String token) {
        LoanRequest loanRequest = loanRequestRepository
                .findFirstByMarketingApproveTrueAndBranchManagerApproveTrueAndBackOfficeIsNullOrderByCreatedAtAsc()
                .orElseThrow(() -> new ResourceNotFoundException("No eligible loan request found"));

        // Set user yang sedang memproses
        String email = jwtUtil.extractEmail(token);
        User backOffice = userService.getUserByEmail(email);

        loanRequest.setBackOffice(backOffice);
        LoanRequest savedLoanRequest = loanRequestRepository.save(loanRequest);

        return LoanRequestDto.fromEntity(savedLoanRequest);
    }

    public Map<String, Object> getLoanRequestByIdDisbursement(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailBackOffice = authentication.getName();

        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (!emailBackOffice.equalsIgnoreCase(loanRequest.getBackOffice().getEmail())) {
            throw new AccessDeniedException("You are not authorized to get this loan request.");
        }

        CustomerDetails customerDetails = customerDetailsService.getByEmail(loanRequest.getCustomer().getEmail());

        LoanRequestDto loanRequestDto = LoanRequestDto.fromEntity(loanRequest);
        CustomerDetailsDto customerDetailsDto = CustomerDetailsDto.fromEntity(customerDetails);

        // Pastikan kamu memasukkan objek yang sesuai dalam map
        Map<String, Object> data = new HashMap<>();
        data.put("loanRequest", loanRequestDto);
        data.put("customerDetails", customerDetailsDto);

        return data;
    }

    public long countWaitingDisbursementRequests() {
        return loanRequestRepository.countByMarketingApproveTrueAndBranchManagerApproveTrueAndBackOfficeIsNull();
    }

    public List<LoanRequestDto> getAllLoanRequestDisbursementOngoing() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailBackOffice = authentication.getName();

        if (emailBackOffice == null || emailBackOffice.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }
        return loanRequestRepository
                .findByBackOfficeEmailAndBackOfficeApproveDisburseIsNull(
                        emailBackOffice)
                .stream()
                .map(LoanRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    public LoanRequestDto updateLoanRequestDisbursement(String id, Map<String, Object> payload, String token) {
        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Loan request not found"));

        if (payload.containsKey("disbursement")) {
            String emailFromToken = jwtUtil.extractEmail(token);

            User backOffice = loanRequest.getBackOffice();
            if (backOffice != null && emailFromToken.equalsIgnoreCase(backOffice.getEmail())) {
                Boolean disbursement = Boolean.parseBoolean(payload.get("disbursement").toString());
                if (disbursement) {
                    loanRequest.setStatus(LoanStatus.APPROVED);
                    CustomerDetails customerDetails = customerDetailsService
                            .getByEmail(loanRequest.getCustomer().getEmail());
                    Double plafond = customerDetails.getAvailablePlafond();
                    Double amount = loanRequest.getAmount();
                    Double interest = loanRequest.getInterest();
                    Double adminFee = loanRequest.getAdminFee();

                    double availablePlafond = (plafond != null ? plafond : 0.0)
                            - (amount != null ? amount : 0.0)
                            - (interest != null ? interest : 0.0)
                            - (adminFee != null ? adminFee : 0.0);

                    if (availablePlafond < 0) {
                        throw new IllegalArgumentException("Plafond tidak mencukupi, pengajuan tidak dapat dicairkan");
                    }

                    customerDetails.setAvailablePlafond(availablePlafond);
                    customerDetailsService.update(customerDetails.getId(), customerDetails);

                    List<FcmToken> tokens = fcmTokenServices.getTokensByEmail(loanRequest.getCustomer().getEmail());

                    // if (tokens.isEmpty()) {
                    //     throw new IllegalArgumentException(
                    //             "FCM token tidak ditemukan untuk email: " + loanRequest.getCustomer().getEmail());
                    // }

                    for (FcmToken fcmToken : tokens) {
                        try {
                            fcmService.sendNotification(
                                    fcmToken.getToken(),
                                    "Pencairan Dana - FINAPay",
                                    "Data telah dicairkan sejumlah: " + loanRequest.getAmount());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Notifikasi gagal terkirim");
                        }
                    }
                } else {
                    loanRequest.setStatus(LoanStatus.REJECTED);
                }
                loanRequest.setBackOfficeApproveDisburse(disbursement);
                loanRequest.setCompletedAt(LocalDateTime.now());
            } else {
                throw new AccessDeniedException("You are not authorized to approve or reject as Marketing.");
            }
        }
        if (payload.containsKey("notes")) {
            String notes = payload.get("notes").toString();
            loanRequest.setBackOfficeNotes(notes);
        }
        LoanRequest savedLoanRequest = loanRequestRepository.save(loanRequest);
        return LoanRequestDto.fromEntity(savedLoanRequest);
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
                    double availablePlafond = customerDetails.getAvailablePlafond() - loanRequest.getAmount()
                            - loanRequest.getInterest();
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

    @Transactional
    public LoanRequestDto getNextLoanRequestForBackOffice() {
        LoanRequest loanRequest = loanRequestRepository
                .findFirstByMarketingApproveTrueAndBranchManagerApproveTrueAndBackOfficeIsNullOrderByCreatedAtAsc()
                .orElseThrow(() -> new ResourceNotFoundException("No loan request available for processing"));

        return LoanRequestDto.fromEntity(loanRequest);
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
