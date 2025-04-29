package com.bcaf.bcapay.dto;

import java.util.UUID;

import com.bcaf.bcapay.models.EmployeeDetails;
import com.bcaf.bcapay.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetailsDto {
    private UUID id;
    private String street;
    private String district;
    private String province;
    private String postalCode;
    private UserDto user;

    public static EmployeeDetailsDto fromEntity(EmployeeDetails entity) {
        return new EmployeeDetailsDto(
            entity.getId(),
            entity.getStreet(),
            entity.getDistrict(),
            entity.getProvince(),
            entity.getPostalCode(),
            UserDto.fromEntity(entity.getUser())
        );
    }

    public EmployeeDetails toEntity(User user) {
        EmployeeDetails entity = new EmployeeDetails();
        entity.setId(this.id);
        entity.setStreet(this.street);
        entity.setDistrict(this.district);
        entity.setProvince(this.province);
        entity.setPostalCode(this.postalCode);
        entity.setUser(user);
        return entity;
    }
}
