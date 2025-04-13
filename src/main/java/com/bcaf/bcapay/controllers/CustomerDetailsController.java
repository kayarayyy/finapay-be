package com.bcaf.bcapay.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.bcaf.bcapay.dto.CustomerDetailsDto;
import com.bcaf.bcapay.dto.ResponseDto;
import com.bcaf.bcapay.services.CustomerDetailsService;

import java.util.List;

@RestController
@RequestMapping("api/v1/customer-details")
public class CustomerDetailsController {

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Secured("FEATURE_MANAGE_LOAN_REQUESTS")
    @GetMapping
    public ResponseEntity<ResponseDto> getAllCustomers() {
        List<CustomerDetailsDto> customers = customerDetailsService.getAll();
        return ResponseEntity.ok(new ResponseDto(200, "success", customers.size() + " customers found", customers));
    }

    // @Secured("FEATURE_VIEW_CUSTOMERS")
    // @GetMapping("/{id}")
    // public ResponseEntity<ResponseDto> getCustomerById(@PathVariable String id) {
    //     CustomerDetailsDto customer = customerDetailsService.getById(id);
    //     return ResponseEntity.ok(new ResponseDto(200, "success", "Customer found", customer));
    // }

    // @Secured("FEATURE_MANAGE_CUSTOMERS")
    // @PostMapping
    // public ResponseEntity<ResponseDto> createCustomer(@RequestBody CustomerDetailsDto customerDto) {
    //     CustomerDetailsDto createdCustomer = customerDetailsService.create(customerDto);
    //     return ResponseEntity.status(HttpStatus.CREATED)
    //             .body(new ResponseDto(201, "success", "Customer created", createdCustomer));
    // }

    // @Secured("FEATURE_MANAGE_CUSTOMERS")
    // @PutMapping("/{id}")
    // public ResponseEntity<ResponseDto> updateCustomer(@PathVariable String id, @RequestBody CustomerDetailsDto customerDto) {
    //     CustomerDetailsDto updatedCustomer = customerDetailsService.update(id, customerDto);
    //     return ResponseEntity.ok(new ResponseDto(200, "success", "Customer updated", updatedCustomer));
    // }

    // @Secured("FEATURE_MANAGE_CUSTOMERS")
    // @DeleteMapping("/{id}")
    // public ResponseEntity<ResponseDto> deleteCustomer(@PathVariable String id) {
    //     customerDetailsService.delete(id);
    //     return ResponseEntity.ok(new ResponseDto(200, "success", "Customer deleted", null));
    // }
}
