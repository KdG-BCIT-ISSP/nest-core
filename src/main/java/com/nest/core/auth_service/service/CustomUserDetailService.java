package com.nest.core.auth_service.service;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            Member member = memberRepository.findByEmail(username);
            return new CustomSecurityUserDetails(member);
        } catch(Exception e) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
    }
}
