package com.nest.core.auth_service.security;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.model.MemberRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];
        try {
            if (jwtUtil.isExpired(token)) {
                log.warn("Token Expired");
                filterChain.doFilter(request, response);

                return;
            }

            String email = jwtUtil.getEmail(token);
            String role = jwtUtil.getRole(token);

            Member member = new Member();
            member.setEmail(email);

            member.setPassword("TempPassword");
            member.setRole(MemberRole.valueOf(role));

            CustomSecurityUserDetails customSecurityUserDetails = new CustomSecurityUserDetails(member);

            Authentication authToken = new UsernamePasswordAuthenticationToken(customSecurityUserDetails, null, customSecurityUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e){
            throw new IOException(e);
        }

        filterChain.doFilter(request, response);
    }
}
