package com.nest.core.member_management_service.dto;

import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.model.MemberRole;
import lombok.Getter;


@Getter
public class GetAllUserProfileResponse {
    private final Long id;
    private final MemberRole role;
    private final String email;
    private final String username;
    private final String region;

    public GetAllUserProfileResponse(Member member) {
        this.id = member.getId();
        this.role = member.getRole();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.region = member.getRegion();
    }

}
