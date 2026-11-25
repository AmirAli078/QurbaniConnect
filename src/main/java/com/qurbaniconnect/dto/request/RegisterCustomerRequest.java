package com.qurbaniconnect.dto.request;

import com.qurbaniconnect.validation.Patterns;
import jakarta.validation.constraints.*;

public record RegisterCustomerRequest(
        @NotBlank @Size(max = 120) String fullName,
        @Email @Size(max = 160) String email,
        @NotBlank @Pattern(regexp = Patterns.PK_PHONE, message = Patterns.PHONE_MSG) String phoneNumber,
        @NotBlank @Pattern(regexp = Patterns.PASSWORD, message = Patterns.PASSWORD_MSG) String password,
        Long cityId, Long areaId, @Size(max = 300) String completeAddress) {}
