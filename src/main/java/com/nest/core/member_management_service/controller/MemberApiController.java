package com.nest.core.member_management_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.member_management_service.dto.*;
import com.nest.core.auth_service.dto.LoginTokenDto;
import com.nest.core.member_management_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Slf4j
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinMemberRequest joinMemberRequest){
        memberService.securityJoin(joinMemberRequest);
        return ResponseEntity.ok("Signup Success");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginMemberRequest loginMemberRequest){
        LoginTokenDto loginTokenDto = memberService.login(loginMemberRequest);
        log.info("TokenDto : {}" , loginTokenDto);
        return ResponseEntity.ok(loginTokenDto);

    }

    @PutMapping("/me")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest, @AuthenticationPrincipal UserDetails userDetails){

        log.info("UserDetails class: {}", userDetails.getClass().getName());

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            String username = customUser.getUsername();
            log.info("UserID {}", userId);
            log.info("UserName {}", username);
            memberService.updateProfile(userId, updateProfileRequest);
            return ResponseEntity.ok("Profile updated");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMember(@AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            GetProfileResponse getProfileResponse = memberService.getMember(userId);
            return ResponseEntity.ok(getProfileResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMembers(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            List<GetAllUserProfileResponse> getAllUserProfileResponse = memberService.getAllMembers(userId);
            return ResponseEntity.ok(getAllUserProfileResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }
}
