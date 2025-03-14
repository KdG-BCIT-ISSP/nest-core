package com.nest.core.member_management_service.dto;

import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.model.MemberRole;
import lombok.Getter;


@Getter
public class GetAllUserProfileResponse {

    private final MemberRole role;
    private final String email;
    private final String username;

    public GetAllUserProfileResponse(Member member) {
        this.role = member.getRole();
        this.email = member.getEmail();
        this.username = member.getUsername();
    }

}
