package com.qurbaniconnect.service;

import com.qurbaniconnect.dto.request.*;
import com.qurbaniconnect.dto.response.*;

public interface AuthService {
    UserResponse registerCustomer(RegisterCustomerRequest request);
    UserResponse registerQasai(RegisterQasaiRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(String refreshToken);
    void logout(Long userId, String refreshToken);
    void forgotPassword(String identifier);
    void resetPassword(ResetPasswordRequest request);
    UserResponse me(Long userId);
}
