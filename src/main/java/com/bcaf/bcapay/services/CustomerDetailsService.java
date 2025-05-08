package com.bcaf.bcapay.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bcaf.bcapay.dto.CustomerDetailsDto;
import com.bcaf.bcapay.exceptions.ResourceNotFoundException;
import com.bcaf.bcapay.models.CustomerDetails;
import com.bcaf.bcapay.repositories.CustomerDetailsRepository;

import jakarta.transaction.Transactional;

@Service
public class CustomerDetailsService {

    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

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

    // public CustomerDetails create(CustomerDetails customerDetails) {
    // return customerDetailsRepository.save(customerDetails);
    // }

    public CustomerDetails update(UUID id, CustomerDetails updatedDetails) {
        CustomerDetails existing = customerDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer details not found!"));
        existing.setAvailablePlafond(updatedDetails.getAvailablePlafond());
        existing.setPlafondPlan(updatedDetails.getPlafondPlan());
        existing.setUser(updatedDetails.getUser());
        return customerDetailsRepository.save(existing);
    }

    public void delete(UUID id) {
        CustomerDetails existing = customerDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer details not found!"));
        customerDetailsRepository.delete(existing);
    }
}
