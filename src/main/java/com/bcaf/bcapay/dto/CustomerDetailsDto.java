package com.bcaf.bcapay.dto;

import java.util.UUID;

import com.bcaf.bcapay.models.CustomerDetails;
import com.bcaf.bcapay.utils.CurrencyUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailsDto {
    private UUID id;
    private String availablePlafond;
    private PlafondDto plafond;
    private UserDto user;
    private String street;
    private String district;
    private String province;
    private String postalCode;
    private Double latitude;
    private Double longitude;


    public static CustomerDetailsDto fromEntity(CustomerDetails entity) {
        return new CustomerDetailsDto(
            entity.getId(),
            CurrencyUtil.toRupiah(entity.getAvailablePlafond()),
            PlafondDto.fromEntity(entity.getPlafondPlan()),
            UserDto.fromEntity(entity.getUser()),
            entity.getStreet(),
            entity.getDistrict(),
            entity.getProvince(),
            entity.getPostalCode(),
            entity.getLatitude(),
            entity.getLongitude()
        );
    }

}
