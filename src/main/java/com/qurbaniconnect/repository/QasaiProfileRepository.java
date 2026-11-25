package com.qurbaniconnect.repository;

import com.qurbaniconnect.entity.*;
import com.qurbaniconnect.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.*;

public interface QasaiProfileRepository extends JpaRepository<QasaiProfile, Long> {
    Optional<QasaiProfile> findByUser_Id(Long userId);
    boolean existsByCnicNumber(String cnicNumber);
    Page<QasaiProfile> findByVerificationStatus(VerificationStatus status, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from QasaiProfile q where q.id = :id")
    Optional<QasaiProfile> findByIdForUpdate(@Param("id") Long id);
}
