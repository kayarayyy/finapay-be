package com.bcaf.finapay.dto;

import java.util.UUID;

import com.bcaf.finapay.models.Plafond;
import com.bcaf.finapay.utils.CurrencyUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlafondDto {
    private UUID id;
    private String amount;
    private String plan;
    private Double annualRate;
    private Double adminRate;
    private String colorStart;
    private String colorEnd;

    public static PlafondDto fromEntity(Plafond entity) {
        return new PlafondDto(
            entity.getId(),
            CurrencyUtil.toRupiah(entity.getAmount()),
            entity.getPlan(),
            entity.getAnnualRate(),
            entity.getAdminRate(),
            entity.getColorStart(),
            entity.getColorEnd()
        );
    }
}
