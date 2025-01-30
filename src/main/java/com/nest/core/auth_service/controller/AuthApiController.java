package com.nest.core.auth_service.controller;

import com.nest.core.auth_service.dto.LoginTokenDto;
import com.nest.core.auth_service.dto.NewTokenDto;
import com.nest.core.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthApiController {
    private final AuthService authService;

    @PostMapping("/getNewAccessToken")
    public ResponseEntity<?> getNewAccessToken(@RequestBody NewTokenDto newTokenDto){
        LoginTokenDto newLoginTokenDto = authService.getNewLoginToken(newTokenDto);
        return ResponseEntity.ok(newLoginTokenDto);
    }
}
