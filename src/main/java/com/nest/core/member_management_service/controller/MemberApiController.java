package com.nest.core.member_management_service.controller;

import com.nest.core.member_management_service.dto.JoinMemberRequest;
import com.nest.core.member_management_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
