package com.lmu.gasbygas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lmu.gasbygas.entity.DeliveryScheduleEntity;
import com.lmu.gasbygas.entity.DeliveryScheduleEntity.ScheduleStatus;
import com.lmu.gasbygas.entity.OutletEntity;

public interface ScheduleRepo extends JpaRepository<DeliveryScheduleEntity,Integer> {

    String FIND_BY_ID = "SELECT * FROM delivery_schedule WHERE id = ?1";
    String FIND_BY_OUTLET_AND_STATUS = "SELECT * FROM delivery_schedule WHERE outlet_id = ? AND status = 'SCHEDULED' ORDER BY delivery_date ASC LIMIT 1;";

    @Query(value = FIND_BY_ID, nativeQuery = true)
    DeliveryScheduleEntity findByScheduleId(int id);

    @Query(value = FIND_BY_OUTLET_AND_STATUS, nativeQuery = true)
    DeliveryScheduleEntity findByOutletAndStatusOrderByDeliveryDateAsc(int outletId, ScheduleStatus scheduled);

    Optional<DeliveryScheduleEntity> findTopByOutletOrderByDeliveryDateDesc(OutletEntity outlet);
    
}
