package com.qurbaniconnect.repository;

import com.qurbaniconnect.entity.*;
import com.qurbaniconnect.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.*;

public interface QasaiAvailabilityRepository extends JpaRepository<QasaiAvailability, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from QasaiAvailability a where a.id = :id")
    Optional<QasaiAvailability> findByIdForUpdate(@Param("id") Long id);

    List<QasaiAvailability> findByQasai_IdOrderByAvailableDateAscStartTimeAsc(Long qasaiId);

    /** Overlap test: existing.start < newEnd AND existing.end > newStart (excluding one slot when updating). */
    @Query("select count(a) > 0 from QasaiAvailability a where a.qasai.id = :qasaiId and a.availableDate = :date " +
           "and a.startTime < :end and a.endTime > :start and (:excludeId is null or a.id <> :excludeId)")
    boolean existsOverlap(@Param("qasaiId") Long qasaiId, @Param("date") LocalDate date,
                          @Param("start") LocalTime start, @Param("end") LocalTime end,
                          @Param("excludeId") Long excludeId);
}
