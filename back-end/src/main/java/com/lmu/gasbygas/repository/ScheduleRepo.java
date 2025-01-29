package com.lmu.gasbygas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lmu.gasbygas.entity.DeliveryScheduleEntity;

public interface ScheduleRepo extends JpaRepository<DeliveryScheduleEntity,Integer> {
    
}
