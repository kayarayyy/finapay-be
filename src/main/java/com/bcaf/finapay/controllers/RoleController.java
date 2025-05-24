package com.bcaf.finapay.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.bcaf.finapay.dto.ResponseDto;
import com.bcaf.finapay.models.Role;
import com.bcaf.finapay.services.RoleService;

@RestController
@RequestMapping("v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Secured("FEATURE_MANAGE_ROLES")
    @GetMapping
    public ResponseEntity<ResponseDto> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(new ResponseDto(200, "success", roles.size() + " roles found", roles));
    }
    
    @Secured("FEATURE_MANAGE_ROLES")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getRoleById(@PathVariable String id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Role found", role));
    }
    
    @Secured("FEATURE_MANAGE_ROLES")
    @PostMapping
    public ResponseEntity<ResponseDto> createRole(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ResponseDto(201, "success", "Role created", createdRole));
    }

    @Secured("FEATURE_MANAGE_ROLES")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateRole(@PathVariable String id, @RequestBody Role role) {
        Role updatedRole = roleService.updateRole(id, role);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Role updated", updatedRole));
    }
    
    @Secured("FEATURE_MANAGE_ROLES")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(new ResponseDto(200, "success", "Role deleted", null));
    }
}
