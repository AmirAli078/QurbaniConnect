package com.qurbaniconnect.repository;

import com.qurbaniconnect.entity.*;
import com.qurbaniconnect.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.*;

public interface QasaiAssignmentRepository extends JpaRepository<QasaiAssignment, Long> {
    List<QasaiAssignment> findByBooking_IdOrderByAssignedAtDesc(Long bookingId);
    Optional<QasaiAssignment> findFirstByBooking_IdAndAssignmentStatusOrderByAssignedAtDesc(Long bookingId, AssignmentStatus status);
}
