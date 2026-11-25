package com.qurbaniconnect.repository;

import com.qurbaniconnect.entity.*;
import com.qurbaniconnect.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.*;

public interface AreaRepository extends JpaRepository<Area, Long> {
    boolean existsByCity_IdAndAreaNameIgnoreCase(Long cityId, String areaName);
    List<Area> findByCity_IdAndActiveTrueOrderByAreaNameAsc(Long cityId);
    Page<Area> findByCity_Id(Long cityId, Pageable pageable);
}
