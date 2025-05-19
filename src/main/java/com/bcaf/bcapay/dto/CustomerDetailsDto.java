package com.bcaf.bcapay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.bcaf.bcapay.models.CustomerDetails;
import com.bcaf.bcapay.models.enums.Gender;
import com.bcaf.bcapay.utils.CurrencyUtil;
import com.bcaf.bcapay.utils.DateFormatterUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailsDto {
    private UUID id;

    private String availablePlafond;
    private String usedPlafond;
    private PlafondDto plafond;
    private UserDto user;

    private String street;
    private String district;
    private String province;
    private String postalCode;

    private Double latitude;
    private Double longitude;

    private Gender gender;
    private LocalDate ttl;
    private String formattedTtl;

    private String noTelp;
    private String nik;
    private String mothersName;
    private String job;
    private BigDecimal salary;
    private String noRek;
    private String houseStatus;

    // File path atau file name untuk dokumen (jika kamu menyimpan path-nya di DB)
    private String ktpUrl;
    private String selfieKtpUrl;
    private String houseUrl;

    public static CustomerDetailsDto fromEntity(CustomerDetails entity) {
        return new CustomerDetailsDto(
            entity.getId(),
            CurrencyUtil.toRupiah(entity.getAvailablePlafond()),
            CurrencyUtil.toRupiah(entity.getPlafondPlan().getAmount() - entity.getAvailablePlafond()),
            PlafondDto.fromEntity(entity.getPlafondPlan()),
            UserDto.fromEntity(entity.getUser()),
            entity.getStreet(),
            entity.getDistrict(),
            entity.getProvince(),
            entity.getPostalCode(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getGender(),
            entity.getTtl(),
            DateFormatterUtil.formatIsoToIndonesianDate(entity.getTtl().toString()),
            entity.getNoTelp(),
            entity.getNik(),
            entity.getMothersName(),
            entity.getJob(),
            entity.getSalary(),
            entity.getNoRek(),
            entity.getHouseStatus(),
            entity.getKtpUrl(),         // Asumsinya kamu simpan file name/path
            entity.getSelfieKtpUrl(),
            entity.getHouseUrl()
        );
    }
}

