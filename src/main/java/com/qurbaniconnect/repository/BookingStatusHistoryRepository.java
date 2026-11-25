package com.qurbaniconnect.repository;

import com.qurbaniconnect.entity.*;
import com.qurbaniconnect.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.*;

public interface BookingStatusHistoryRepository extends JpaRepository<BookingStatusHistory, Long> {
    List<BookingStatusHistory> findByBooking_IdOrderByCreatedAtAscIdAsc(Long bookingId);
}
