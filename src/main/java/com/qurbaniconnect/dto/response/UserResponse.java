package com.qurbaniconnect.dto.response;

import com.qurbaniconnect.enums.VerificationStatus;
import java.util.List;

public record UserResponse(Long id, String fullName, String email, String phoneNumber, String accountStatus,
                           List<String> roles, Long customerProfileId, Long qasaiProfileId,
                           VerificationStatus verificationStatus) {}
