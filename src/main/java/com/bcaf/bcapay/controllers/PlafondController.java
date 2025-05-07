package com.bcaf.bcapay.controllers;

import com.bcaf.bcapay.dto.PlafondDto;
import com.bcaf.bcapay.dto.ResponseDto;
import com.bcaf.bcapay.models.Plafond;
import com.bcaf.bcapay.services.PlafondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/plafonds")
public class PlafondController {

    @Autowired
    private PlafondService plafondService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<PlafondDto>>> getAllPlafonds() {
        List<Plafond> data = plafondService.getAllPlafonds();
        List<PlafondDto> plafonds = data.stream()
                .map(PlafondDto::fromEntity)
                .toList();
        return ResponseEntity.ok(new ResponseDto<>(200, "success", plafonds.size() + " plafonds found", plafonds));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<Plafond>> getPlafondById(@PathVariable UUID id) {
        Plafond data = plafondService.getPlafondById(id);
        return ResponseEntity.ok(new ResponseDto<>(200, "success", "plafond found", data));
    }

    @GetMapping("/plan/{plan}")
    public ResponseEntity<ResponseDto<Plafond>> getPlafondByPlan(@PathVariable String plan) {
        Plafond data = plafondService.getPlafondByPlan(plan);
        return ResponseEntity.ok(new ResponseDto<>(200, "success", "plafond found", data));
    }

    @Secured("FEATURE_MANAGE_PLAFONDS")
    @PostMapping
    public ResponseEntity<ResponseDto<Plafond>> createPlafond(@RequestBody Plafond plafond) {
        Plafond data = plafondService.createPlafond(plafond);
        return ResponseEntity.ok(new ResponseDto<>(201, "created", "plafond created successfully", data));
    }

    @Secured("FEATURE_MANAGE_PLAFONDS")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<Plafond>> updatePlafond(@PathVariable UUID id,
            @RequestBody Plafond updatedPlafond) {
        Plafond data = plafondService.updatePlafond(id, updatedPlafond);
        return ResponseEntity.ok(new ResponseDto<>(200, "updated", "plafond updated successfully", data));
    }

    @Secured("FEATURE_MANAGE_PLAFONDS")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> deletePlafond(@PathVariable UUID id) {
        plafondService.deletePlafond(id);
        return ResponseEntity.ok(new ResponseDto<>(200, "deleted", "plafond deleted successfully", null));
    }
}
