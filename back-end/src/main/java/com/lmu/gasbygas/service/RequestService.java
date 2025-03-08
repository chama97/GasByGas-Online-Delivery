package com.lmu.gasbygas.service;

import com.lmu.gasbygas.dto.request.GasRequestReqDTO;
import com.lmu.gasbygas.util.ResponseUtil;

public interface RequestService {

    ResponseUtil requestGas(GasRequestReqDTO reqDto);
    ResponseUtil getAllRequests();
    ResponseUtil getAllRequestByClientId(int id, GasRequestReqDTO reqDto);
    ResponseUtil getAllRequestByOutletId(int id, GasRequestReqDTO reqDto);
    ResponseUtil updateRequestStatus(int id, String status);
    ResponseUtil cancelRequest(int id);
    
}
