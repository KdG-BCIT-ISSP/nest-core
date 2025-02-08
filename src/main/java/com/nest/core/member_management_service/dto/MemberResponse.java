package com.nest.core.member_management_service.dto;

import com.nest.core.member_management_service.model.MemberRole;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String email;
    private String username;
    private String avatar;
    private String region;
}