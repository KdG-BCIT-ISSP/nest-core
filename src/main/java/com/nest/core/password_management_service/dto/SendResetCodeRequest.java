package com.nest.core.password_management_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SendResetCodeRequest {
    private String email;
}
