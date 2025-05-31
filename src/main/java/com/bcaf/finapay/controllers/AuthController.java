package com.bcaf.finapay.controllers;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcaf.finapay.dto.AuthDto;
import com.bcaf.finapay.dto.ResponseDto;
import com.bcaf.finapay.models.User;
import com.bcaf.finapay.services.AuthService;
import com.bcaf.finapay.services.FcmTokenServices;
import com.bcaf.finapay.services.ResetPasswordService;
import com.bcaf.finapay.utils.ResponseUtil;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ResetPasswordService resetPasswordService;

    @Autowired
    private FcmTokenServices fcmTokenServices;

    // @Secured("MANAGE_USERS")
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody Map<String, Object> payload) {
        AuthDto authDto = authService.login(
                (String) payload.get("email"),
                (String) payload.get("password"),
                (String) payload.get("fcmToken"));
        return ResponseUtil.success(authDto, "Login successful");

    }

    @PostMapping("/login-employee")
    public ResponseEntity<ResponseDto> loginEmployee(@RequestBody Map<String, Object> payload) {
        AuthDto authDto = authService.login_employee(
                (String) payload.get("nip"),
                (String) payload.get("password"));
        return ResponseUtil.success(authDto, "Login successful");

    }

    @PostMapping("/login-google")
    public ResponseEntity<ResponseDto> loginWithGoogle(@RequestBody Map<String, Object> payload) {
        AuthDto authDto = authService.login_with_google((String) payload.get("tokenId"),
                (String) payload.get("fcmToken"));
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

    @DeleteMapping("/logout/{fcmToken}")
    public ResponseEntity<ResponseDto> logout(@PathVariable String fcmToken){
        fcmTokenServices.deleteToken(fcmToken);
        return ResponseUtil.success(null, "Logout successful");
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseDto> putChangePassword(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> payload) {
        authService.changePassword(payload, token);

        return ResponseUtil.success(true, "Ubah kata berhasil!");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDto> getResetPasswordLink(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        resetPasswordService.getResetPasswordLink(email);
        return ResponseUtil.created(null, "Instruksi reset password telah dikirim ke " + email);
    }

    @PutMapping("/reset-password/{id}")
    public ResponseEntity<ResponseDto> setNewPasswordByResetPasswordEmail(@RequestBody Map<String, Object> payload,
            @PathVariable String id) {
        String email = (String) payload.get("email");
        String new_password = (String) payload.get("new_password");
        String confirm_password = (String) payload.get("confirm_password");

        resetPasswordService.setNewPasswordByResetPasswordEmail(id, email,
                new_password, confirm_password);
        return ResponseUtil.success(null, "Reset password successful");
    }

    @PostMapping("/generate-activation-link")
    public ResponseEntity<ResponseDto> getActivationLink(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        authService.generateActivationLink(email);
        return ResponseUtil.created(null, "Instruksi aktivasi akun telah dikirimkan ke " + email);
    }

    @GetMapping("/activate/{id}")
    public ResponseEntity<ResponseDto> activateAccount(@PathVariable String id) {
        authService.activate(id);
        return ResponseUtil.success(true, "Aktivasi akun berhasil dilakukan");
    }
}
