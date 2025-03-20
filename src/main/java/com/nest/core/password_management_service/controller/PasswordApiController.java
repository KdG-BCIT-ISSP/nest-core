package com.nest.core.password_management_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.password_management_service.dto.ChangePasswordRequest;
import com.nest.core.password_management_service.dto.ResetPasswordRequest;
import com.nest.core.password_management_service.dto.SendResetCodeRequest;
import com.nest.core.password_management_service.service.PasswordService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/password")
@Slf4j
public class PasswordApiController {
    private final PasswordService passwordService;

    @PutMapping("/change")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ChangePasswordRequest changeRequest) {

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            passwordService.changePassword(customUser.getUserId(), changeRequest);
            return ResponseEntity.ok("Password changed");
        } else {
            return ResponseEntity.badRequest().body("Cannot change password of unauthenticated user");
        }
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody SendResetCodeRequest codeRequest) {
        passwordService.sendEmail(codeRequest);
        return ResponseEntity.ok("Not implemented yet");
    }

    @PutMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetRequest) {
        passwordService.resetPassword(resetRequest);
        return ResponseEntity.ok("Not implemented yet");
    }
}
