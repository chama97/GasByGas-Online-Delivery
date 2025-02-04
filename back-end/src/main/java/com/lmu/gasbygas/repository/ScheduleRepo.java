package com.lmu.gasbygas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lmu.gasbygas.entity.DeliveryScheduleEntity;

public interface ScheduleRepo extends JpaRepository<DeliveryScheduleEntity,Integer> {

    String FIND_BY_ID = "SELECT * FROM delivery_schedule WHERE id = ?1";

    @Query(value = FIND_BY_ID, nativeQuery = true)
    DeliveryScheduleEntity findByScheduleId(int id);
    
}
