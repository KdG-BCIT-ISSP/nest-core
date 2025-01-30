package com.nest.core.member_management_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginMemberRequest {
    private String email;
    private String password;
}
