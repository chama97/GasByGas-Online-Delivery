package com.lmu.gasbygas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lmu.gasbygas.entity.OutletEntity;

public interface OutletRepo extends JpaRepository<OutletEntity,Integer> {

    
    String FIND_BY_LOCATION = "SELECT * FROM outlet WHERE location = ?1";
    String FIND_BY_ID = "SELECT * FROM outlet WHERE id = ?1";
    String FIND_BY_STATUS = "SELECT * FROM outlet WHERE status = ?1";

    @Query(value = FIND_BY_LOCATION, nativeQuery = true)
    boolean findByLocation(String location);

    @Query(value = FIND_BY_ID, nativeQuery = true)
    OutletEntity findByOutletId(int id);

    @Query(value = FIND_BY_STATUS, nativeQuery = true)
    List<OutletEntity> findAllNameByStatus(Integer active);

    boolean existsByLocation(String location);
    
}
