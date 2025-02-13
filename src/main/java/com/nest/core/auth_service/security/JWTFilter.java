package com.nest.core.auth_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.model.MemberRole;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];
        try {
            if (jwtUtil.isExpired(token)) {
                log.warn("Token Expired");
                sendErrorResponse(response, "Token expired", HttpStatus.UNAUTHORIZED);
                return;
            }

            String email = jwtUtil.getEmail(token);

            CustomSecurityUserDetails customSecurityUserDetails = (CustomSecurityUserDetails) userDetailsService.loadUserByUsername(email);

            Authentication authToken = new UsernamePasswordAuthenticationToken(customSecurityUserDetails, null, customSecurityUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (ExpiredJwtException e) {
            log.warn("Token Expired Exception: {}", e.getMessage());
            sendErrorResponse(response, "Token expired", HttpStatus.UNAUTHORIZED);
            return;
        } catch (Exception e){
            log.error("JWT processing error", e);
            sendErrorResponse(response, "Invalid token", HttpStatus.UNAUTHORIZED);
            SecurityContextHolder.clearContext();
            return;
        }

        filterChain.doFilter(request, response);
    }


    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", message);
        errorDetails.put("status", status.value());

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
