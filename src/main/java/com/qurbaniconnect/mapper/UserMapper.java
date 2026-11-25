package com.qurbaniconnect.mapper;

import com.qurbaniconnect.dto.response.UserResponse;
import com.qurbaniconnect.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserMapper {
    public UserResponse toResponse(User u, CustomerProfile customer, QasaiProfile qasai) {
        log.debug("[Mapper] Mapping user to response: userId={}, fullName={}", u.getId(), u.getFullName());
        
        var roles = u.getRoles().stream().map(r -> r.getRoleName().name()).sorted().toList();
        log.debug("[Mapper] User roles: {}", roles);
        
        Long customerId = customer == null ? null : customer.getId();
        log.debug("[Mapper] Customer profile id: {}", customerId);
        
        Long qasaiId = qasai == null ? null : qasai.getId();
        log.debug("[Mapper] Qasai profile id: {}", qasaiId);
        
        var verificationStatus = qasai == null ? null : qasai.getVerificationStatus();
        log.debug("[Mapper] Qasai verification status: {}", verificationStatus);
        
        UserResponse response = new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getPhoneNumber(), u.getAccountStatus().name(),
                roles,
                customerId,
                qasaiId,
                verificationStatus);
        
        log.debug("[Mapper] User response mapped successfully for userId={}", u.getId());
        return response;
    }
}
