package com.lmu.gasbygas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lmu.gasbygas.entity.DeliveryStockEntity;

public interface StockRepo extends JpaRepository<DeliveryStockEntity, Integer> {
    
}
