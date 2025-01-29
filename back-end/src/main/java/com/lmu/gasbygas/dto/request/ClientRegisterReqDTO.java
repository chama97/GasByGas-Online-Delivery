package com.lmu.gasbygas.dto.request;

import com.lmu.gasbygas.entity.RoleEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRegisterReqDTO {

    private String name;
    private String email;
    private String password;
    private String address;
    private String contact;
    private RoleEntity roleId;
    private String nic;
    private String registrationNumber;
    private String certification;
    
}
