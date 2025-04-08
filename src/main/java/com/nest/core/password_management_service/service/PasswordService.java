package com.nest.core.password_management_service.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nest.core.auth_service.security.JWTUtil;
import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.password_management_service.dto.ChangePasswordRequest;
import com.nest.core.password_management_service.dto.ResetPasswordRequest;
import com.nest.core.password_management_service.dto.SendResetCodeRequest;
import com.nest.core.password_management_service.exception.EmailNotFoundException;
import com.nest.core.password_management_service.exception.InvalidNewPasswordException;
import com.nest.core.password_management_service.exception.InvalidOldPasswordException;
import com.nest.core.password_management_service.exception.InvalidResetCodeException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PasswordService {
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void changePassword(Long userId, ChangePasswordRequest changeRequest) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("User not found"));

        if (!bCryptPasswordEncoder.matches(changeRequest.getOldPassword(), member.getPassword())) {
            throw new InvalidOldPasswordException("Old password does not match");
        }
        if (bCryptPasswordEncoder.matches(changeRequest.getNewPassword(), member.getPassword())) {
            throw new InvalidNewPasswordException("New password cannot be the same as the old password");
        }

        member.setPassword(bCryptPasswordEncoder.encode(changeRequest.getNewPassword()));

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            log.warn("Error updating password: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while updating password");
        }
    }

    public String generateResetToken(SendResetCodeRequest codeRequest) {
        Member member = memberRepository.findByEmail(codeRequest.getEmail());
        if (member == null) {
            throw new EmailNotFoundException("Email not associated with an account");
        }
        return jwtUtil.createResetToken(codeRequest.getEmail(), 1000 * 60 * 10L);
    }

    public void resetPassword(ResetPasswordRequest resetRequest, String resetUID) {
        String token = jwtUtil.getResetToken(resetUID);
        if (token == null || jwtUtil.isExpired(token)) {
            throw new InvalidResetCodeException("Code is either expired or invalid");
        }
        String email = jwtUtil.getEmail(token);
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberNotFoundException("Associated reset token email could not be found");
        }
        if (bCryptPasswordEncoder.matches(resetRequest.getNewPassword(), member.getPassword())) {
            throw new InvalidNewPasswordException("New password cannot be the same as the old password");
        }

        member.setPassword(bCryptPasswordEncoder.encode(resetRequest.getNewPassword()));

        try {
            memberRepository.save(member);

        } catch (Exception e) {
            log.warn("Error updating password: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while updating password");
        }
    }

}
