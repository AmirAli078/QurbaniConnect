package com.qurbaniconnect.repository;

import com.qurbaniconnect.entity.*;
import com.qurbaniconnect.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByRoles_RoleName(RoleName roleName);
    @Query("select u from User u left join fetch u.roles where u.id = :id")
    Optional<User> findWithRolesById(@Param("id") Long id);
}
