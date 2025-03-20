package com.nest.core.password_management_service.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.password_management_service.dto.ChangePasswordRequest;
import com.nest.core.password_management_service.dto.ResetPasswordRequest;
import com.nest.core.password_management_service.dto.SendResetCodeRequest;
import com.nest.core.password_management_service.exception.InvalidNewPasswordException;
import com.nest.core.password_management_service.exception.InvalidOldPasswordException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PasswordService {
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

    public void sendEmail(SendResetCodeRequest codeRequest) {
        // TODO: Find a way to send an email to the email of the request
    }

    public void resetPassword(ResetPasswordRequest resetRequest) {
        // TODO: Find a way to generate a code and verify the code is correct
    }

}
