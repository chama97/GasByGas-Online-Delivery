package com.lmu.gasbygas.dto.response;

import com.lmu.gasbygas.entity.OutletManagerEntity;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutletResDTO {

    private int outletId;
    private String name;
    private Object managerName;
    private String location;
    private String contact;
    private int status;

}
