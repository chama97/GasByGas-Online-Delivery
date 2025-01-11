package com.lmu.gasbygas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lmu.gasbygas.entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity,Integer> {

    UserEntity findFirstByUsername(String username);

}