package com.nest.core.member_management_service.dto;

import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.model.MemberRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinMemberRequest {

    private String email;
    private String password;
    private String username;
    private String avatar;

    public Member toEntity(){
        return Member.builder()
                .email(this.email)
                .avatar(this.avatar)
                .password(this.password)
                .username(this.username)
                .role(MemberRole.USER)
                .build();
    }
}
