package com.nest.core.member_management_service.service;

import com.nest.core.auth_service.security.JWTUtil;
import com.nest.core.member_management_service.dto.JoinMemberRequest;
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
            return;
        }

        joinMemberRequest.setPassword(bCryptPasswordEncoder.encode(joinMemberRequest.getPassword()));


        Member member = joinMemberRequest.toEntity();

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

}
