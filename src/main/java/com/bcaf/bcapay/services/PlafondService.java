package com.bcaf.bcapay.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcaf.bcapay.models.Plafond;
import com.bcaf.bcapay.models.enums.Plan;
import com.bcaf.bcapay.repositories.PlafondRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlafondService {

    @Autowired
    private PlafondRepository plafondRepository;

    public List<Plafond> getAllPlafonds() {
        return plafondRepository.findAll();
    }

    public Plafond getPlafondById(UUID id) {
        return plafondRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plafond not found"));
    }

    public Plafond getPlafondByPlan(Plan plan) {
        return plafondRepository.findByPlan(plan)
                .orElseThrow(() -> new RuntimeException("Plafond not found for plan: " + plan));
    }

    public Plafond createPlafond(Plafond plafond) {
        return plafondRepository.save(plafond);
    }

    public Plafond updatePlafond(UUID id, Plafond updatedPlafond) {
        Plafond existing = getPlafondById(id);
        existing.setAmount(updatedPlafond.getAmount());
        existing.setAnnualRate(updatedPlafond.getAnnualRate());
        existing.setPlan(updatedPlafond.getPlan());
        return plafondRepository.save(existing);
    }

    public void deletePlafond(UUID id) {
        plafondRepository.deleteById(id);
    }
}
