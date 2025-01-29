package com.lmu.gasbygas.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutletReqDTO {

    private int id;
    private String name;
    private String managerName;
    private String location;
    private String contact;
    private int status;
    
}
