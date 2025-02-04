package com.lmu.gasbygas.service;

import com.lmu.gasbygas.dto.request.ScheduleReqDTO;
import com.lmu.gasbygas.util.ResponseUtil;

public interface ScheduleService {

    ResponseUtil scheduleDelivery(ScheduleReqDTO scheduleReqDTO);
    ResponseUtil getAllSchedules();
    ResponseUtil updateScheduleStatus(int id, String status);
    ResponseUtil updateSchedule(int id, ScheduleReqDTO scheduleReqDTO);
    ResponseUtil deleteSchedule(int id);

}
