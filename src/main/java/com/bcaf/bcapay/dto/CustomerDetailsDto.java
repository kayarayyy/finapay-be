package com.bcaf.bcapay.dto;

import java.util.UUID;

import com.bcaf.bcapay.models.CustomerDetails;
import com.bcaf.bcapay.models.Plafond;
import com.bcaf.bcapay.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailsDto {
    private UUID id;
    private double availablePlafond;
    private UUID plafondId;
    private UserDto user;

    public static CustomerDetailsDto fromEntity(CustomerDetails entity) {
        return new CustomerDetailsDto(
            entity.getId(),
            entity.getAvailablePlafond(),
            entity.getPlafondPlan() != null ? entity.getPlafondPlan().getId() : null,
            UserDto.fromEntity(entity.getUser())
        );
    }

    public CustomerDetails toEntity(Plafond plafond, User user) {
        CustomerDetails entity = new CustomerDetails();
        entity.setId(this.id);
        entity.setAvailablePlafond(this.availablePlafond);
        entity.setPlafondPlan(plafond);
        entity.setUser(user);
        return entity;
    }
}
