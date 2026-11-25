package com.qurbaniconnect.dto.request;

import com.qurbaniconnect.validation.Patterns;
import jakarta.validation.constraints.*;

public record RegisterQasaiRequest(
        @NotBlank @Size(max = 120) String fullName,
        @Email @Size(max = 160) String email,
        @NotBlank @Pattern(regexp = Patterns.PK_PHONE, message = Patterns.PHONE_MSG) String phoneNumber,
        @NotBlank @Pattern(regexp = Patterns.PASSWORD, message = Patterns.PASSWORD_MSG) String password,
        @NotNull Long cityId, Long areaId,
        @NotBlank @Pattern(regexp = Patterns.CNIC, message = "CNIC must be 13 digits (xxxxx-xxxxxxx-x)") String cnicNumber,
        @NotNull @Min(0) @Max(80) Integer yearsOfExperience,
        @Size(max = 1000) String professionalDescription) {}
