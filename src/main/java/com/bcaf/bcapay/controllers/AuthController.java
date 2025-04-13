package com.bcaf.bcapay.controllers;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcaf.bcapay.dto.AuthDto;
import com.bcaf.bcapay.dto.ResponseDto;
import com.bcaf.bcapay.models.User;
import com.bcaf.bcapay.services.AuthService;
import com.bcaf.bcapay.services.ResetPasswordService;
import com.bcaf.bcapay.utils.ResponseUtil;

import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ResetPasswordService resetPasswordService;

    // @Secured("MANAGE_USERS")
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody Map<String, Object> payload) {
        AuthDto authDto = authService.login(
                (String) payload.get("email"),
                (String) payload.get("password"));
        return ResponseUtil.success(authDto, "Login successful");

    }

    @PostMapping("/login-employee")
    public ResponseEntity<ResponseDto> loginEmployee(@RequestBody Map<String, Object> payload) {
        AuthDto authDto = authService.login_employee(
                (String) payload.get("nip"),
                (String) payload.get("password"));
        return ResponseUtil.success(authDto, "Login successful");

    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> payload) {
        User user = authService.register(payload, token);
        return ResponseUtil.created(new AuthDto(
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.isActive(),
                token,
                Arrays.asList("")), "Register successful");

    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseDto> putChangePassword(@RequestHeader(value = "Authorization", required = false) String token,
    @RequestBody Map<String, Object> payload) {
        authService.changePassword(payload, token);
        
        return ResponseUtil.success(null, "change password success");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<ResponseDto> getResetPasswordLink(@RequestBody Map<String, Object> payload) {
        resetPasswordService.getResetPasswordLink((String) payload.get("email"));
        return ResponseUtil.created(null, "Reset password Email sent successful");
    }

    @PostMapping("/reset-password/{id}")
    public ResponseEntity<ResponseDto> setNewPasswordByResetPasswordEmail(@RequestBody Map<String, Object> payload,
            @PathVariable String id) {
        resetPasswordService.setNewPasswordByResetPasswordEmail(id, (String) payload.get("email"),
                (String) payload.get("new_password"));
        return ResponseUtil.success(null, "Reset password successful");
    }
}
