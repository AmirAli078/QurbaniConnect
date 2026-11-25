package com.qurbaniconnect.dto.request;

import com.qurbaniconnect.validation.Patterns;
import jakarta.validation.constraints.*;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank @Pattern(regexp = Patterns.PASSWORD, message = Patterns.PASSWORD_MSG) String newPassword) {}
