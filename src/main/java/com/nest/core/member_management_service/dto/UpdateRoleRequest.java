package com.nest.core.member_management_service.dto;

import com.nest.core.member_management_service.model.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateRoleRequest {
    private MemberRole memberRole;

}
