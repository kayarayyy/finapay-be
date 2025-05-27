package com.bcaf.finapay.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcaf.finapay.dto.BranchDto;
import com.bcaf.finapay.dto.ResponseDto;
import com.bcaf.finapay.models.Branch;
import com.bcaf.finapay.services.BranchService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("v1/branches")
public class BranchController {
    @Autowired
    private BranchService branchService;

    @Secured("FEATURE_MANAGE_BRANCHES")
    @GetMapping
    public ResponseEntity<ResponseDto> getAllBranches() {
        List<BranchDto> brances = branchService.getAllBranches();
        return ResponseEntity.ok(new ResponseDto(200, "success", brances.size() + " brances found", brances));
    }

    @Secured("FEATURE_MANAGE_BRANCHES")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getBranchById(@PathVariable String id) {
        BranchDto branch = branchService.getBranchById(id);
        return ResponseEntity.ok(new ResponseDto(200, "success", "brances found", branch));
    }

    @Secured("FEATURE_MANAGE_BRANCHES")
    @PostMapping
    public ResponseEntity<ResponseDto> createBranch(@RequestBody Map<String, Object> payload) {
        Branch createdBranch = branchService.createBranch(payload);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(201, "success", "Branch created", createdBranch));
    }

    @Secured("FEATURE_MANAGE_BRANCHES")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateBranch(
            @PathVariable String id,
            @RequestBody Map<String, Object> payload) {
        Branch updatedBranch = branchService.editBranch(id, payload);

        return ResponseEntity.ok(
                new ResponseDto(200, "success", "Branch updated", updatedBranch));
    }

    @Secured("FEATURE_MANAGE_BRANCHES")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteBranch(@PathVariable String id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Branch deleted", null));
    }

    @Secured("FEATURE_MANAGE_BRANCHES")
    @PostMapping("/{branchId}/assign-manager")
    public ResponseEntity<ResponseDto> assignBranchManager(
            @PathVariable String branchId,
            @RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        Branch updated = branchService.assignBranchManager(branchId, email);
        return ResponseEntity.ok(
                new ResponseDto(200, "success", "Branch manager assigned", updated));
    }

    @Secured("FEATURE_MANAGE_BRANCHES")
    @PostMapping("/{branchId}/assign-marketing")
    public ResponseEntity<ResponseDto> assignMarketing(
            @PathVariable String branchId,
            @RequestBody Map<String, List<String>> payload) {
        List<String> emails = payload.get("emails");
        if (emails == null || emails.isEmpty()) {
            throw new IllegalArgumentException("Email list is required");
        }

        Branch updated = branchService.assignMarketing(branchId, emails);
        return ResponseEntity.ok(
                new ResponseDto(200, "success", emails.size() + " marketing(s) assigned", updated));
    }

}
