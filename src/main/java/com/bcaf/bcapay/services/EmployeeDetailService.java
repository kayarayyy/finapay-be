package com.bcaf.bcapay.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bcaf.bcapay.dto.EmployeeDetailsDto;
import com.bcaf.bcapay.exceptions.ResourceNotFoundException;
import com.bcaf.bcapay.models.EmployeeDetails;
import com.bcaf.bcapay.repositories.EmployeeDetailsRepoitory;

@Service
public class EmployeeDetailService {
    @Autowired
    private EmployeeDetailsRepoitory employeeDetailsRepoitory;

    public EmployeeDetailsDto getEmployeeProfileByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailEmployee = authentication != null ? authentication.getName() : null;

        if (emailEmployee == null || emailEmployee.isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }

        EmployeeDetails employeeDetails = employeeDetailsRepoitory.findByUserEmail(emailEmployee)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return EmployeeDetailsDto.fromEntity(employeeDetails);
    }

}
