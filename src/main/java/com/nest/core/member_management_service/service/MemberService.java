package com.nest.core.member_management_service.service;

import com.nest.core.auth_service.security.JWTUtil;
import com.nest.core.member_management_service.dto.JoinMemberRequest;
import com.nest.core.member_management_service.dto.LoginMemberRequest;
import com.nest.core.auth_service.dto.LoginTokenDto;
import com.nest.core.member_management_service.exception.DuplicateMemberFoundException;
import com.nest.core.member_management_service.exception.InvalidPasswordException;
import com.nest.core.member_management_service.exception.MemberNotFoundException;
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
                        findMember.getEmail(),
                        String.valueOf(findMember.getRole()),
                        1000 * 60 * 30L),
                jwtUtil.createRefreshToken(
                        findMember.getEmail(),
                        String.valueOf(findMember.getRole()),
                        1000 * 60 * 60 * 7L
                )
        );
    }

}
