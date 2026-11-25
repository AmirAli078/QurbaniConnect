package com.qurbaniconnect.repository;

import com.qurbaniconnect.entity.*;
import com.qurbaniconnect.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.*;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByBooking_Id(Long bookingId);
    @Query("select avg(r.rating) from Review r where r.qasai.id = :qasaiId and r.visible = true")
    Double averageRating(@Param("qasaiId") Long qasaiId);
    long countByQasai_IdAndVisibleTrue(Long qasaiId);
}
