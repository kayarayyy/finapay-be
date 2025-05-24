package com.bcaf.finapay.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcaf.finapay.dto.EmployeeDetailsDto;
import com.bcaf.finapay.dto.ResponseDto;
import com.bcaf.finapay.services.EmployeeDetailService;

@RestController
@RequestMapping("v1/employee-details")
public class EmployeeDetailsController {
    @Autowired
    private EmployeeDetailService employeeDetailService;

    @Secured({"FEATURE_GET_EMPLOYEE_DETAILS", "FEATURE_MANAGE_USERS"})
    @GetMapping
    public ResponseEntity<ResponseDto> getEmployeeProfileByEmail() {
        EmployeeDetailsDto employeeDetailsDto = employeeDetailService.getEmployeeProfileByEmail();
        return ResponseEntity.ok(new ResponseDto(200, "success", "User found", employeeDetailsDto));
    }

    @Secured({"FEATURE_CREATE_EMPLOYEE_DETAILS", "FEATURE_MANAGE_USERS"})
    @PostMapping
    public ResponseEntity<ResponseDto> createEmployeeDetails(@RequestBody Map<String, Object> payload) {
        EmployeeDetailsDto employeeDetailsDto = employeeDetailService.createEmployeeDetails(payload);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Berhasil melengkapi detail pegawai", employeeDetailsDto));
    }
}
