package com.bcaf.bcapay.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bcaf.bcapay.dto.CustomerDetailsDto;
import com.bcaf.bcapay.dto.ResponseDto;
import com.bcaf.bcapay.models.CustomerDetails;
import com.bcaf.bcapay.models.enums.Gender;
import com.bcaf.bcapay.services.CustomerDetailsService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1/customer-details")
public class CustomerDetailsController {

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Secured("FEATURE_MANAGE_CUSTOMER_DETAILS")
    @GetMapping
    public ResponseEntity<ResponseDto> getAllCustomers() {
        List<CustomerDetailsDto> customers = customerDetailsService.getAll();
        return ResponseEntity.ok(new ResponseDto(200, "success", customers.size() + " customers found", customers));
    }

    @Secured("FEATURE_GET_CUSTOMER_DETAILS_BY_EMAIL")
    @GetMapping("/by-email")
    public ResponseEntity<ResponseDto> getCustomerByEmail() {
        CustomerDetails customer = customerDetailsService.getCustomerDetails();
        CustomerDetailsDto customerDto = CustomerDetailsDto.fromEntity(customer);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Customer found", customerDto));
    }
    // @Secured("FEATURE_VIEW_CUSTOMERS")
    // @GetMapping("/{id}")
    // public ResponseEntity<ResponseDto> getCustomerById(@PathVariable String id) {
    // CustomerDetailsDto customer = customerDetailsService.getById(id);
    // return ResponseEntity.ok(new ResponseDto(200, "success", "Customer found",
    // customer));
    // }

    @Secured({ "FEATURE_MANAGE_CUSTOMERS", "FEATURE_CREATE_CUSTOMER_DETAILS" })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> createCustomer(
            @RequestPart("street") String streetStr,
            @RequestPart("district") String districtStr,
            @RequestPart("province") String provinceStr,
            @RequestPart("postal_code") String postalCodeStr,
            @RequestPart("latitude") String latitudeStr,
            @RequestPart("longitude") String longitudeStr,
            @RequestPart("gender") String genderStr,
            @RequestPart("ttl") String ttlStr,
            @RequestPart("no_telp") String noTelpStr,
            @RequestPart("nik") String nikStr,
            @RequestPart("mothers_name") String mothersNameStr,
            @RequestPart("job") String jobStr,
            @RequestPart("salary") String salaryStr,
            @RequestPart("no_rek") String noRekStr,
            @RequestPart("house_status") String houseStatusStr,
            @RequestPart("selfieKtp") MultipartFile selfieKtp,
            @RequestPart("house") MultipartFile house,
            @RequestPart("ktp") MultipartFile ktp) {

        Double latitude = Double.parseDouble(latitudeStr);
        Double longitude = Double.parseDouble(longitudeStr);
        Gender gender = Gender.valueOf(genderStr.toUpperCase()); // pastikan string-nya sesuai nama enum

        // Parse tanggal lahir (ttl), misal dalam format yyyy-MM-dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate ttl = LocalDate.parse(ttlStr, formatter);
        Double salary = Double.parseDouble(salaryStr);

        CustomerDetailsDto payload = new CustomerDetailsDto();
        payload.setStreet(streetStr);
        payload.setDistrict(districtStr);
        payload.setProvince(provinceStr);
        payload.setPostalCode(postalCodeStr);
        payload.setLatitude(latitude);
        payload.setLongitude(longitude);
        payload.setGender(gender);
        payload.setTtl(ttl);
        payload.setNoTelp(noTelpStr);
        payload.setNik(nikStr);
        payload.setMothersName(mothersNameStr);
        payload.setJob(jobStr);
        payload.setSalary(salary);
        payload.setNoRek(noRekStr);
        payload.setHouseStatus(houseStatusStr);

        Map<String, Object> multipartFile = new HashMap<>();
        multipartFile.put("ktp", ktp);
        multipartFile.put("selfieKtp", selfieKtp);
        multipartFile.put("house", house);

        CustomerDetailsDto createdCustomer = customerDetailsService.create(payload, multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(201, "success", "Customer created", createdCustomer));
    }

    // @Secured("FEATURE_MANAGE_CUSTOMERS")
    // @PutMapping("/{id}")
    // public ResponseEntity<ResponseDto> updateCustomer(@PathVariable String id,
    // @RequestBody CustomerDetailsDto customerDto) {
    // CustomerDetailsDto updatedCustomer = customerDetailsService.update(id,
    // customerDto);
    // return ResponseEntity.ok(new ResponseDto(200, "success", "Customer updated",
    // updatedCustomer));
    // }

    // @Secured("FEATURE_MANAGE_CUSTOMERS")
    // @DeleteMapping("/{id}")
    // public ResponseEntity<ResponseDto> deleteCustomer(@PathVariable String id) {
    // customerDetailsService.delete(id);
    // return ResponseEntity.ok(new ResponseDto(200, "success", "Customer deleted",
    // null));
    // }
}
