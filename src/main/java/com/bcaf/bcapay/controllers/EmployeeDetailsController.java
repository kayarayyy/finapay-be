package com.bcaf.bcapay.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcaf.bcapay.dto.EmployeeDetailsDto;
import com.bcaf.bcapay.dto.ResponseDto;
import com.bcaf.bcapay.services.EmployeeDetailService;

@RestController
@RequestMapping("api/v1/employee-details")
public class EmployeeDetailsController {
    @Autowired
    private EmployeeDetailService employeeDetailService;

    @GetMapping
    public ResponseEntity<ResponseDto> getEmployeeProfileByEmail() {
        EmployeeDetailsDto employeeDetailsDto = employeeDetailService.getEmployeeProfileByEmail();
        return ResponseEntity.ok(new ResponseDto(200, "success", "User found", employeeDetailsDto));
    }
}
