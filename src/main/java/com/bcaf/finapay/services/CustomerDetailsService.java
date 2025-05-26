package com.bcaf.finapay.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bcaf.finapay.dto.CustomerDetailsDto;
import com.bcaf.finapay.exceptions.ResourceNotFoundException;
import com.bcaf.finapay.models.CustomerDetails;
import com.bcaf.finapay.models.Plafond;
import com.bcaf.finapay.models.User;
import com.bcaf.finapay.repositories.CustomerDetailsRepository;

import jakarta.transaction.Transactional;

@Service
public class CustomerDetailsService {

    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    private FileStorageServiceImpl fileStorageService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlafondService plafondService;

    public List<CustomerDetailsDto> getAll() {
        return customerDetailsRepository.findAll().stream().map(CustomerDetailsDto::fromEntity)
                .collect(Collectors.toList());
    }

    public CustomerDetailsDto getById(UUID id) {
        CustomerDetails customerDetails = customerDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer details not found!"));
        return CustomerDetailsDto.fromEntity(customerDetails);
    }

    public CustomerDetails getByEmail(String email) {
        CustomerDetails customerDetails = customerDetailsRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer details not found for email: " + email
                        + " Complete your details identity for apply loan"));
        return customerDetails;
    }

    public CustomerDetails getCustomerDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !StringUtils.hasText(authentication.getName())) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }

        final String emailCustomer = authentication.getName();

        return customerDetailsRepository.findByUserEmail(emailCustomer)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer details not found for email: " + emailCustomer
                                + ". Complete your identity details to apply for a loan."));
    }

    public CustomerDetailsDto create(CustomerDetailsDto payload, Map<String, Object> multipartFile) {

        // Ambil user dari authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (customerDetailsRepository.existsByUser(user)) {
            throw new IllegalStateException("Customer details for user already exist.");
        }

        Plafond plan = plafondService.getPlafondByPlan("BRONZE");

        CustomerDetails customerDetails = new CustomerDetails();

        customerDetails.setPlafondPlan(plan);
        customerDetails.setAvailablePlafond(plan.getAmount());
        customerDetails.setUser(user);
        // Set field dari payload
        customerDetails.setStreet(payload.getStreet());
        customerDetails.setDistrict(payload.getDistrict());
        customerDetails.setProvince(payload.getProvince());
        customerDetails.setPostalCode(payload.getPostalCode());
        customerDetails.setLatitude(payload.getLatitude());
        customerDetails.setLongitude(payload.getLongitude());
        customerDetails.setGender(payload.getGender());
        customerDetails.setTtl(payload.getTtl());
        customerDetails.setNoTelp(payload.getNoTelp());
        customerDetails.setNik(payload.getNik());
        customerDetails.setMothersName(payload.getMothersName());
        customerDetails.setJob(payload.getJob());
        customerDetails.setSalary(payload.getSalary());
        customerDetails.setNoRek(payload.getNoRek());
        customerDetails.setHouseStatus(payload.getHouseStatus());

        // Simpan file dan dapatkan path/url
        MultipartFile ktpFile = (MultipartFile) multipartFile.get("ktp");
        MultipartFile selfieFile = (MultipartFile) multipartFile.get("selfieKtp");
        MultipartFile houseFile = (MultipartFile) multipartFile.get("house");

        String ktpUrl = fileStorageService.saveImage(ktpFile, "ktp_" + user.getId(), "ktp");
        String selfieUrl = fileStorageService.saveImage(selfieFile, "selfie_" + user.getId(), "selfie");
        String houseUrl = fileStorageService.saveImage(houseFile, "house_" + user.getId(), "house");

        customerDetails.setKtpUrl(ktpUrl);
        customerDetails.setSelfieKtpUrl(selfieUrl);
        customerDetails.setHouseUrl(houseUrl);

        // Simpan ke DB
        CustomerDetails saved = customerDetailsRepository.save(customerDetails);

        return CustomerDetailsDto.fromEntity(saved);
    }

    public CustomerDetails update(UUID id, CustomerDetails updatedDetails) {
        CustomerDetails existing = customerDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer details not found!"));
        existing.setAvailablePlafond(updatedDetails.getAvailablePlafond());
        existing.setPlafondPlan(updatedDetails.getPlafondPlan());
        existing.setUser(updatedDetails.getUser());
        return customerDetailsRepository.save(existing);
    }

    public void delete(String id) {
        CustomerDetails existing = customerDetailsRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Customer details not found!"));
        customerDetailsRepository.delete(existing);
    }
}
