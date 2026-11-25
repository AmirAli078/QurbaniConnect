package com.qurbaniconnect.repository;

import com.qurbaniconnect.entity.*;
import com.qurbaniconnect.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.*;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String ref);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);
    boolean existsByAvailability_IdAndBookingStatusIn(Long availabilityId, Collection<BookingStatus> statuses);
}
