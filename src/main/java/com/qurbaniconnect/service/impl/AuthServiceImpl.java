package com.qurbaniconnect.service.impl;

import com.qurbaniconnect.dto.request.*;
import com.qurbaniconnect.dto.response.*;
import com.qurbaniconnect.entity.*;
import com.qurbaniconnect.enums.*;
import com.qurbaniconnect.exception.*;
import com.qurbaniconnect.mapper.UserMapper;
import com.qurbaniconnect.repository.*;
import com.qurbaniconnect.security.JwtService;
import com.qurbaniconnect.service.AuthService;
import com.qurbaniconnect.util.TokenHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerProfileRepository customerRepository;
    private final QasaiProfileRepository qasaiRepository;
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Value("${app.jwt.refresh-expiration-ms}") private long refreshExpirationMs;
    @Value("${app.password-reset.expiry-minutes}") private long resetExpiryMinutes;
    @Value("${app.password-reset.log-token}") private boolean logResetToken;

    @Override
    @Transactional
    public UserResponse registerCustomer(RegisterCustomerRequest r) {
        log.info("[Service] Starting customer registration for email={}, phone={}", r.email(), r.phoneNumber());
        
        log.debug("[Service] Resolving city for customerId={}", r.cityId());
        City city = r.cityId() == null ? null : requireCity(r.cityId());
        log.debug("[Service] City resolved: {}", city != null ? city.getCityName() : "null");
        
        log.debug("[Service] Resolving area for areaId={}", r.areaId());
        Area area = r.areaId() == null ? null : requireArea(r.areaId(), city);
        log.debug("[Service] Area resolved: {}", area != null ? area.getAreaName() : "null");
        
        log.info("[Service] Creating user with role=ROLE_CUSTOMER");
        User user = createUser(r.fullName(), r.email(), r.phoneNumber(), r.password(), RoleName.ROLE_CUSTOMER);
        log.info("[Service] User created with id={}", user.getId());
        
        log.debug("[Service] Creating customer profile");
        CustomerProfile profile = new CustomerProfile();
        profile.setUser(user);
        profile.setCity(city);
        profile.setArea(area);
        profile.setCompleteAddress(r.completeAddress());
        customerRepository.save(profile);
        log.info("[Service] Customer profile saved with id={}", profile.getId());
        
        log.info("[Service] Customer registration completed for userId={}", user.getId());
        return userMapper.toResponse(user, profile, null);
    }

    @Override
    @Transactional
    public UserResponse registerQasai(RegisterQasaiRequest r) {
        log.info("[Service] Starting Qasai registration for email={}, phone={}", r.email(), r.phoneNumber());
        
        log.debug("[Service] Resolving city for cityId={}", r.cityId());
        City city = requireCity(r.cityId());
        log.debug("[Service] City resolved: {}", city.getCityName());
        
        log.debug("[Service] Resolving area for areaId={}", r.areaId());
        Area area = r.areaId() == null ? null : requireArea(r.areaId(), city);
        log.debug("[Service] Area resolved: {}", area != null ? area.getAreaName() : "null");
        
        log.debug("[Service] Normalizing CNIC");
        String cnic = normalizeCnic(r.cnicNumber());
        log.debug("[Service] Normalized CNIC: {}", cnic);
        
        log.debug("[Service] Checking for duplicate CNIC");
        if (qasaiRepository.existsByCnicNumber(cnic)) {
            log.warn("[Service] Duplicate CNIC found: {}", cnic);
            throw new DuplicateResourceException("A Qasai with this CNIC is already registered");
        }
        log.debug("[Service] CNIC is unique");
        
        log.info("[Service] Creating user with role=ROLE_QASAI");
        User user = createUser(r.fullName(), r.email(), r.phoneNumber(), r.password(), RoleName.ROLE_QASAI);
        log.info("[Service] User created with id={}", user.getId());
        
        log.debug("[Service] Creating Qasai profile");
        QasaiProfile profile = new QasaiProfile();
        profile.setUser(user);
        profile.setCity(city);
        profile.setArea(area);
        profile.setCnicNumber(cnic);
        profile.setYearsOfExperience(r.yearsOfExperience());
        profile.setProfessionalDescription(r.professionalDescription());
        profile.setVerificationStatus(VerificationStatus.PENDING);
        qasaiRepository.save(profile);
        log.info("[Service] Qasai profile saved with id={}, status=PENDING", profile.getId());
        
        log.info("[Service] Qasai registration completed for userId={}, awaiting admin verification", user.getId());
        return userMapper.toResponse(user, null, profile);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest r) {
        log.info("[Service] Login attempt for identifier={}", r.identifier());
        
        log.debug("[Service] Looking up user by identifier");
        User user = findByIdentifier(r.identifier()).orElse(null);
        
        // Same message for unknown user / wrong password so accounts cannot be enumerated.
        if (user == null || !passwordEncoder.matches(r.password(), user.getPassword())) {
            log.warn("[Service] Login failed for identifier={} - invalid credentials", r.identifier());
            throw new UnauthorizedException("Invalid credentials");
        }
        log.debug("[Service] Password verified for userId={}", user.getId());
        
        log.debug("[Service] Checking account status for userId={}, status={}", user.getId(), user.getAccountStatus());
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            log.warn("[Service] Login failed for userId={} - account is inactive", user.getId());
            throw new ForbiddenException("Account is inactive. Please contact support");
        }
        
        log.info("[Service] Issuing tokens for userId={}", user.getId());
        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        log.info("[Service] Token refresh request");
        
        log.debug("[Service] Hashing refresh token and looking up in database");
        RefreshToken stored = refreshTokenRepository.findByTokenHash(TokenHasher.sha256Hex(refreshToken))
                .orElseThrow(() -> {
                    log.warn("[Service] Refresh token not found in database");
                    return new UnauthorizedException("Invalid refresh token");
                });
        log.debug("[Service] Refresh token found for userId={}", stored.getUser().getId());
        
        log.debug("[Service] Checking token validity: revoked={}, expired={}", stored.isRevoked(), stored.getExpiryDate().isBefore(LocalDateTime.now()));
        if (stored.isRevoked() || stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("[Service] Refresh token invalid: revoked={}, expired={}", stored.isRevoked(), stored.getExpiryDate().isBefore(LocalDateTime.now()));
            throw new UnauthorizedException("Refresh token expired or revoked");
        }
        
        log.debug("[Service] Loading user with roles for userId={}", stored.getUser().getId());
        User user = userRepository.findWithRolesById(stored.getUser().getId()).orElseThrow();
        
        log.debug("[Service] Checking account status for userId={}, status={}", user.getId(), user.getAccountStatus());
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            log.warn("[Service] Token refresh failed for userId={} - account is inactive", user.getId());
            throw new ForbiddenException("Account is inactive");
        }
        
        log.info("[Service] Revoking old refresh token and issuing new tokens for userId={}", user.getId());
        stored.setRevoked(true);                       // rotation: one-time use
        return issueTokens(user);
    }

    @Override
    @Transactional
    public void logout(Long userId, String refreshToken) {
        log.info("[Service] Logout request for userId={}", userId);
        
        log.debug("[Service] Looking up refresh token by hash");
        boolean revoked = refreshTokenRepository.findByTokenHash(TokenHasher.sha256Hex(refreshToken))
                .filter(t -> t.getUser().getId().equals(userId))
                .map(t -> {
                    t.setRevoked(true);
                    log.info("[Service] Refresh token revoked for userId={}", userId);
                    return true;
                })
                .orElse(false);
        
        if (!revoked) {
            log.warn("[Service] No matching refresh token found for userId={}", userId);
        }
    }

    @Override
    @Transactional
    public void forgotPassword(String identifier) {
        log.info("[Service] Password reset request for identifier={}", identifier);
        
        // Always succeeds silently to prevent account enumeration.
        findByIdentifier(identifier).ifPresent(user -> {
            log.info("[Service] Account found for password reset, userId={}", user.getId());
            String token = TokenHasher.newOpaqueToken();
            PasswordResetToken t = new PasswordResetToken();
            t.setUser(user);
            t.setTokenHash(TokenHasher.sha256Hex(token));
            t.setExpiryDate(LocalDateTime.now().plusMinutes(resetExpiryMinutes));
            resetTokenRepository.save(t);
            log.info("[Service] Password reset token saved for userId={}, expiresAt={}", user.getId(), t.getExpiryDate());
            // Phase 1 has no email/SMS provider; the token is logged in dev/test only.
            if (logResetToken) log.info("PASSWORD RESET TOKEN for userId={}: {}", user.getId(), token);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest r) {
        log.info("[Service] Password reset execution with token");
        
        log.debug("[Service] Looking up password reset token");
        PasswordResetToken t = resetTokenRepository.findByTokenHash(TokenHasher.sha256Hex(r.token()))
                .orElseThrow(() -> {
                    log.warn("[Service] Password reset token not found");
                    return new ValidationException("Invalid or expired reset token");
                });
        
        log.debug("[Service] Checking token validity: used={}, expired={}", t.isUsed(), t.getExpiryDate().isBefore(LocalDateTime.now()));
        if (t.isUsed() || t.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("[Service] Password reset token invalid: used={}, expired={}", t.isUsed(), t.getExpiryDate().isBefore(LocalDateTime.now()));
            throw new ValidationException("Invalid or expired reset token");
        }
        
        User user = t.getUser();
        log.info("[Service] Resetting password for userId={}", user.getId());
        user.setPassword(passwordEncoder.encode(r.newPassword()));
        log.info("[Service] Password encoded and saved for userId={}", user.getId());
        
        t.setUsed(true);
        log.debug("[Service] Marking reset token as used");
        
        log.info("[Service] Revoking all refresh tokens for userId={}", user.getId());
        refreshTokenRepository.revokeAllForUser(user.getId());
        log.info("[Service] Password reset completed for userId={}", user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me(Long userId) {
        log.info("[Service] Fetching user profile for userId={}", userId);
        
        log.debug("[Service] Loading user with roles for userId={}", userId);
        User user = userRepository.findWithRolesById(userId).orElseThrow(() -> {
            log.warn("[Service] User not found for userId={}", userId);
            return new ResourceNotFoundException("User not found");
        });
        
        log.debug("[Service] Loading customer profile for userId={}", userId);
        var customerProfile = customerRepository.findByUser_Id(userId).orElse(null);
        log.debug("[Service] Customer profile found: {}", customerProfile != null ? "yes" : "no");
        
        log.debug("[Service] Loading Qasai profile for userId={}", userId);
        var qasaiProfile = qasaiRepository.findByUser_Id(userId).orElse(null);
        log.debug("[Service] Qasai profile found: {}", qasaiProfile != null ? "yes" : "no");
        
        log.info("[Service] User profile fetched for userId={}", userId);
        return userMapper.toResponse(user, customerProfile, qasaiProfile);
    }

    // ---------- helpers ----------

    private User createUser(String fullName, String email, String phone, String rawPassword, RoleName roleName) {
        log.info("[Service] Creating user with role={}, email={}, phone={}", roleName, email, phone);
        
        log.debug("[Service] Normalizing phone number");
        String normalizedPhone = normalizePhone(phone);
        log.debug("[Service] Normalized phone: {}", normalizedPhone);
        
        log.debug("[Service] Normalizing email");
        String normalizedEmail = (email == null || email.isBlank()) ? null : email.trim().toLowerCase();
        log.debug("[Service] Normalized email: {}", normalizedEmail);
        
        log.debug("[Service] Checking for duplicate phone number");
        if (userRepository.existsByPhoneNumber(normalizedPhone)) {
            log.warn("[Service] Duplicate phone number found: {}", normalizedPhone);
            throw new DuplicateResourceException("Phone number is already registered");
        }
        
        log.debug("[Service] Checking for duplicate email");
        if (normalizedEmail != null && userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.warn("[Service] Duplicate email found: {}", normalizedEmail);
            throw new DuplicateResourceException("Email is already registered");
        }
        
        log.debug("[Service] Creating user entity");
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPhoneNumber(normalizedPhone);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.getRoles().add(roleRepository.findByRoleName(roleName).orElseThrow());
        
        log.debug("[Service] Saving user to database");
        User savedUser = userRepository.save(user);
        log.info("[Service] User created and saved with id={}, role={}", savedUser.getId(), roleName);
        return savedUser;
    }

    private AuthResponse issueTokens(User user) {
        log.info("[Service] Issuing tokens for userId={}", user.getId());
        
        log.debug("[Service] Generating new refresh token");
        String refresh = TokenHasher.newOpaqueToken();
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(TokenHasher.sha256Hex(refresh));
        rt.setExpiryDate(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));
        refreshTokenRepository.save(rt);
        log.info("[Service] Refresh token saved for userId={}, expiresAt={}", user.getId(), rt.getExpiryDate());
        
        log.debug("[Service] Building user response for userId={}", user.getId());
        UserResponse ur = userMapper.toResponse(user, customerRepository.findByUser_Id(user.getId()).orElse(null),
                qasaiRepository.findByUser_Id(user.getId()).orElse(null));
        
        log.debug("[Service] Generating access token for userId={}", user.getId());
        String accessToken = jwtService.generateAccessToken(user.getId());
        log.info("[Service] Access token generated for userId={}, expirationMs={}", user.getId(), jwtService.getAccessExpirationMs());
        
        return new AuthResponse(accessToken, refresh, "Bearer",
                jwtService.getAccessExpirationMs(), ur);
    }

    private Optional<User> findByIdentifier(String identifier) {
        log.debug("[Service] Finding user by identifier={}", identifier);
        String id = identifier.trim();
        if (id.contains("@")) {
            log.debug("[Service] Identifier looks like email, searching by email");
            return userRepository.findByEmailIgnoreCase(id);
        }
        log.debug("[Service] Identifier looks like phone, searching by phone");
        return userRepository.findByPhoneNumber(normalizePhone(id));
    }

    private City requireCity(Long id) {
        log.debug("[Service] Looking up city by id={}", id);
        City c = cityRepository.findById(id).orElseThrow(() -> {
            log.warn("[Service] City not found for id={}", id);
            return new ResourceNotFoundException("City not found");
        });
        log.debug("[Service] City found: {}", c.getCityName());
        if (!c.isActive()) {
            log.warn("[Service] City is not active: id={}, name={}", id, c.getId());
            throw new ValidationException("City is not active");
        }
        return c;
    }

    private Area requireArea(Long id, City city) {
        log.debug("[Service] Looking up area by id={}", id);
        Area a = areaRepository.findById(id).orElseThrow(() -> {
            log.warn("[Service] Area not found for id={}", id);
            return new ResourceNotFoundException("Area not found");
        });
        log.debug("[Service] Area found: {}", a.getAreaName());
        if (!a.isActive()) {
            log.warn("[Service] Area is not active: id={}, name={}", id, a.getAreaName());
            throw new ValidationException("Area is not active");
        }
        if (city != null && !a.getCity().getId().equals(city.getId())) {
            log.warn("[Service] Area id={} does not belong to city id={}", id, city.getId());
            throw new ValidationException("Area does not belong to the selected city");
        }
        return a;
    }

    /** Stores Pakistani numbers in the local 03XXXXXXXXX form. */
    public static String normalizePhone(String phone) {
        String p = phone.trim().replace(" ", "").replace("-", "");
        return p.startsWith("+92") ? "0" + p.substring(3) : p;
    }

    public static String normalizeCnic(String cnic) {
        String d = cnic.replace("-", "");
        return d.substring(0, 5) + "-" + d.substring(5, 12) + "-" + d.substring(12);
    }
}
