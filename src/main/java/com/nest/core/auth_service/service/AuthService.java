package com.nest.core.auth_service.service;

import com.nest.core.auth_service.dto.LoginTokenDto;
import com.nest.core.auth_service.dto.NewTokenDto;
import com.nest.core.auth_service.exception.RefreshTokenExpiredException;
import com.nest.core.auth_service.security.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JWTUtil jwtUtil;

    public LoginTokenDto getNewLoginToken(NewTokenDto newTokenDto){

        if (jwtUtil.isExpired(newTokenDto.getRefreshToken())){
            throw new RefreshTokenExpiredException("Refresh Token has expired. Please log in again.");
        }

        return new LoginTokenDto(
                jwtUtil.createJwt(
                        jwtUtil.getUserId(newTokenDto.getRefreshToken()),
                        jwtUtil.getEmail(newTokenDto.getRefreshToken()),
                        jwtUtil.getRole(newTokenDto.getRefreshToken()),
                        1000 * 60 * 30L),
                jwtUtil.createRefreshToken(
                        jwtUtil.getUserId(newTokenDto.getRefreshToken()),
                        jwtUtil.getEmail(newTokenDto.getRefreshToken()),
                        jwtUtil.getRole(newTokenDto.getRefreshToken()),
                        1000 * 60 * 60 * 7L
                )
        );
    }
}
