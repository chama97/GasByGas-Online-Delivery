package com.lmu.gasbygas.service;

import com.lmu.gasbygas.dto.request.ClientRegisterReqDTO;
import com.lmu.gasbygas.util.ResponseUtil;

public interface ClientService {

  ResponseUtil registerNewClient(ClientRegisterReqDTO dto) throws Exception;
  ResponseUtil searchClientByUsername(String username);
    
}
