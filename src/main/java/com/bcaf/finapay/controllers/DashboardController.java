package com.bcaf.finapay.controllers;

import com.bcaf.finapay.dto.DashboardDto;
import com.bcaf.finapay.dto.ResponseDto;
import com.bcaf.finapay.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Secured("FEATURE_DASHBOARD_SUPERADMIN")
    @GetMapping("/superadmin")
    public ResponseEntity<ResponseDto> getDashboardSuperadmin() {
        DashboardDto data = dashboardService.getDashboardSuperadmin();
        return ResponseEntity.ok(new ResponseDto<>(200, "success", "Dashboard data fetched", data));
    }

    @Secured("FEATURE_DASHBOARD_MARKETING")
    @GetMapping("/marketing")
    public ResponseEntity<ResponseDto> getDashboardMarketing() {
        DashboardDto data = dashboardService.getDashboardMarketing();
        return ResponseEntity.ok(new ResponseDto<>(200, "success", "Dashboard data fetched", data));
    }
    @Secured("FEATURE_DASHBOARD_BRANCHMANAGER")
    @GetMapping("/branch-manager")
    public ResponseEntity<ResponseDto> getDashboardBranchManager() {
        DashboardDto data = dashboardService.getDashboardBranchManager();
        return ResponseEntity.ok(new ResponseDto<>(200, "success", "Dashboard data fetched", data));
    }
    @Secured("FEATURE_DASHBOARD_BACKOFFICE")
    @GetMapping("/back-office")
    public ResponseEntity<ResponseDto> getDashboardBackOffice() {
        DashboardDto data = dashboardService.getDashboardBackOffice();
        return ResponseEntity.ok(new ResponseDto<>(200, "success", "Dashboard data fetched", data));
    }
}
