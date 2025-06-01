package com.bcaf.finapay.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcaf.finapay.dto.DashboardDto;
import com.bcaf.finapay.models.Branch;
import com.bcaf.finapay.models.LoanRequest;
import com.bcaf.finapay.models.User;
import com.bcaf.finapay.repositories.BranchRepository;
import com.bcaf.finapay.repositories.LoanRequestRepository;
import com.bcaf.finapay.repositories.UserRepository;

@Service
public class DashboardService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRequestRepository loanRequestRepository;

    public DashboardDto getDashboardSuperadmin() {
        return getCommonDashboard();
    }

    public DashboardDto getDashboardMarketing() {
        return getCommonDashboard();
    }

    public DashboardDto getDashboardBranchManager() {
        return getCommonDashboard();
    }

    public DashboardDto getDashboardBackOffice() {
        return getCommonDashboard();
    }

    private DashboardDto getCommonDashboard() {
        List<User> users = userRepository.findAll();
        List<Branch> branches = branchRepository.findAll();
        List<LoanRequest> loanRequests = loanRequestRepository.findAll();

        return DashboardDto.fromEntity(users, branches, loanRequests);
    }
}
