package com.bcaf.bcapay.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bcaf.bcapay.dto.EmployeeDetailsDto;
import com.bcaf.bcapay.exceptions.ResourceNotFoundException;
import com.bcaf.bcapay.models.Branch;
import com.bcaf.bcapay.models.EmployeeDetails;
import com.bcaf.bcapay.models.User;
import com.bcaf.bcapay.repositories.EmployeeDetailsRepoitory;

@Service
public class EmployeeDetailService {
    @Autowired
    private EmployeeDetailsRepoitory employeeDetailsRepoitory;

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    public EmployeeDetailsDto getEmployeeProfileByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailEmployee = authentication != null ? authentication.getName() : null;

        if (emailEmployee == null || emailEmployee.isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }

        EmployeeDetails employeeDetails = employeeDetailsRepoitory.findByUserEmail(emailEmployee)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (employeeDetails.getUser().getBranch() == null) {
            Branch branch = branchService.getBranchByBranchManagerId(employeeDetails.getUser().getId());
            employeeDetails.getUser().setBranch(branch);
        }
        return EmployeeDetailsDto.fromEntity(employeeDetails);
    }

    public EmployeeDetailsDto createEmployeeDetails(Map<String, Object> payload) {
        // Ambil email user dari security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email == null || email.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated.");
        }

        // Ambil user dari database
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User with email " + email + " not found.");
        }

        // Mapping dari payload ke entitas EmployeeDetails
        EmployeeDetails details = new EmployeeDetails();
        details.setStreet((String) payload.get("street"));
        details.setDistrict((String) payload.get("district"));
        details.setProvince((String) payload.get("province"));
        details.setPostalCode((String) payload.get("postalCode"));
        details.setUser(user);

        // Simpan ke database
        EmployeeDetails savedDetails = employeeDetailsRepoitory.save(details);

        // Konversi ke DTO
        return EmployeeDetailsDto.fromEntity(savedDetails);
    }

}
