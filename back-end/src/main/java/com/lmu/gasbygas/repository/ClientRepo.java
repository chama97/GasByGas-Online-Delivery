package com.lmu.gasbygas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lmu.gasbygas.entity.ClientEntity;

public interface ClientRepo extends JpaRepository<ClientEntity,Integer> {

    String FIND_BY_EMAIL = "SELECT * FROM client WHERE email = ?1";

    @Query(value = FIND_BY_EMAIL, nativeQuery = true)
    ClientEntity findClientByEmail(String email);
    
}
