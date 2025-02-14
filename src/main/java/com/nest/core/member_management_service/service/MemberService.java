package com.nest.core.member_management_service.service;

import com.nest.core.auth_service.security.JWTUtil;
import com.nest.core.member_management_service.dto.GetProfileResponse;
import com.nest.core.member_management_service.dto.JoinMemberRequest;
import com.nest.core.member_management_service.dto.LoginMemberRequest;
import com.nest.core.member_management_service.dto.UpdateProfileRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nest.core.auth_service.dto.LoginTokenDto;
import com.nest.core.member_management_service.exception.DuplicateMemberFoundException;
import com.nest.core.member_management_service.exception.InvalidPasswordException;
import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.member_management_service.exception.ProfileUpdateException;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;


    public void securityJoin(JoinMemberRequest joinMemberRequest){
        if(memberRepository.existsByEmail(joinMemberRequest.getEmail())){
            throw new DuplicateMemberFoundException("Email is already registered.");
        }

        joinMemberRequest.setPassword(bCryptPasswordEncoder.encode(joinMemberRequest.getPassword()));
        Member member = joinMemberRequest.toEntity();

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            log.warn("Error saving member: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while saving member");
        }
    }

    public LoginTokenDto login(LoginMemberRequest loginMemberRequest) {

        log.info("LoginRequest : {}", loginMemberRequest.getEmail());

        Member findMember = memberRepository.findByEmail(loginMemberRequest.getEmail());

        if(findMember == null){
            throw new MemberNotFoundException("Member not found.");
        }

        if (!bCryptPasswordEncoder.matches(loginMemberRequest.getPassword(), findMember.getPassword())) {
            throw new InvalidPasswordException("Invalid password.");
        }

        return new LoginTokenDto(
                jwtUtil.createJwt(
                        String.valueOf(findMember.getId()),
                        findMember.getEmail(),
                        String.valueOf(findMember.getRole()),
                        1000 * 60 * 30L),
                jwtUtil.createRefreshToken(
                        String.valueOf(findMember.getId()),
                        findMember.getEmail(),
                        String.valueOf(findMember.getRole()),
                        1000 * 60 * 60 * 7L
                )
        );
    }

    public void updateProfile(Long userId, UpdateProfileRequest updateMemberRequest) {
        log.info("Updating profile for UserId: {}", userId);

        Member findMember = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found for ID: " + userId));

        boolean updated = false;

        if (updateMemberRequest.getUsername() != null && !updateMemberRequest.getUsername().equals(findMember.getUsername())) {
            findMember.setUsername(updateMemberRequest.getUsername());
            updated = true;
        }

        if (updateMemberRequest.getRegion() != null && !updateMemberRequest.getRegion().equals(findMember.getRegion())) {
            findMember.setRegion(updateMemberRequest.getRegion());
            updated = true;
        }

        if (updateMemberRequest.getAvatar() != null && !updateMemberRequest.getAvatar().equals(findMember.getAvatar().get("image").asText())) {
            ObjectMapper mapper = new ObjectMapper();
            findMember.setAvatar(mapper.createObjectNode().put("image", updateMemberRequest.getAvatar()));
            updated = true;
        }

        if (!updated) {
            log.info("No changes detected for UserId: {}", userId);
            return;
        }

        try {
            memberRepository.save(findMember);
            log.info("Profile updated successfully for UserId: {}", userId);
        } catch (Exception e) {
            log.error("Error updating profile for UserId {}: {}", userId, e.getMessage());
            throw new ProfileUpdateException("Unexpected error occurred while updating profile");
        }
    }

    public GetProfileResponse getMember(Long userId) {

        return memberRepository.findById(userId)
                .map(GetProfileResponse::new)
                .orElseThrow(() -> new MemberNotFoundException("Member not found for ID: " + userId));
    }
}
