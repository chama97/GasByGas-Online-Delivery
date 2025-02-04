package com.lmu.gasbygas.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lmu.gasbygas.dto.request.ScheduleReqDTO;
import com.lmu.gasbygas.dto.request.StockReqDTO;
import com.lmu.gasbygas.dto.response.ScheduleResDTO;
import com.lmu.gasbygas.dto.response.StockResDTO;
import com.lmu.gasbygas.entity.DeliveryScheduleEntity;
import com.lmu.gasbygas.entity.DeliveryStockEntity;
import com.lmu.gasbygas.entity.GasEntity;
import com.lmu.gasbygas.entity.OutletEntity;
import com.lmu.gasbygas.repository.GasRepo;
import com.lmu.gasbygas.repository.OutletRepo;
import com.lmu.gasbygas.repository.ScheduleRepo;
import com.lmu.gasbygas.repository.StockRepo;
import com.lmu.gasbygas.service.ScheduleService;
import com.lmu.gasbygas.util.ResponseUtil;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    ScheduleRepo scheduleRepo;

    @Autowired
    OutletRepo outletRepo;

    @Autowired
    StockRepo stockRepo;

    @Autowired
    GasRepo gasRepo;

    @Override
    public ResponseUtil scheduleDelivery(ScheduleReqDTO scheduleReqDTO) {

        OutletEntity outlet = outletRepo.findById(scheduleReqDTO.getOutletId())
                .orElseThrow(() -> new RuntimeException("Outlet not found with ID: " + scheduleReqDTO.getOutletId()));

        DeliveryScheduleEntity scheduleEntity = new DeliveryScheduleEntity();
        scheduleEntity.setOutlet(outlet);
        scheduleEntity.setDeliveryDate(scheduleReqDTO.getDeliveryDate());
        scheduleEntity.setDeliveryTime(scheduleReqDTO.getDeliveryTime());
        try {
            scheduleEntity.setStatus(DeliveryScheduleEntity.ScheduleStatus.valueOf(scheduleReqDTO.getStatus()));
        } catch (IllegalArgumentException e) {
            return new ResponseUtil(400, "Invalid schedule status provided", null);
        }

        DeliveryScheduleEntity scheduleSave = scheduleRepo.save(scheduleEntity);
        if (scheduleSave != null) {
            List<DeliveryStockEntity> stockEntities = new ArrayList<>();
            if (scheduleReqDTO.getStockList() != null && !scheduleReqDTO.getStockList().isEmpty()) {
                for (StockReqDTO stockDTO : scheduleReqDTO.getStockList()) {
                    GasEntity gas = gasRepo.findById(stockDTO.getGasId())
                            .orElseThrow(() -> new RuntimeException("Gas not found with ID: " + stockDTO.getGasId()));

                    DeliveryStockEntity stock = new DeliveryStockEntity();
                    stock.setSchedule(scheduleEntity);
                    stock.setGas(gas);
                    stock.setQty(stockDTO.getQuantity());

                    stockEntities.add(stock);
                }
                List<DeliveryStockEntity> stockList = stockRepo.saveAll(stockEntities);
                if (stockList != null) {
                    return new ResponseUtil(200, "Schedule Saved Successfuly", null);
                } else {
                    throw new RuntimeException("Failed to Scheduled delivery stock");
                }
            }
            throw new RuntimeException("Failed to Schedule. Delivery stock is empty");
        }
        throw new RuntimeException("Failed to scheduled delivery");
    }

    @Override
    public ResponseUtil getAllSchedules() {
        List<DeliveryScheduleEntity> scheduleEntities = scheduleRepo.findAll();
        List<ScheduleResDTO> scheduleResDTOs = new ArrayList<>();
        if (scheduleEntities != null) {
            for (DeliveryScheduleEntity dse : scheduleEntities) {
                ScheduleResDTO sdto = new ScheduleResDTO();
                sdto.setScheduleId(dse.getScheduleId());
                sdto.setOutletId(dse.getOutlet().getOutletId());
                sdto.setDeliveryDate(dse.getDeliveryDate());
                sdto.setDeliveryTime(dse.getDeliveryTime());
                sdto.setStatus(dse.getStatus().name());
                sdto.setStockList(dse.getStockList().stream().map(stock -> new StockResDTO(
                        stock.getGas().getGasId(),
                        stock.getGas().getType(),
                        stock.getQty())).collect(Collectors.toList()));
                scheduleResDTOs.add(sdto);
            }
            return new ResponseUtil(200, "Success", scheduleResDTOs);
        } else {
            throw new RuntimeException("Unsuccess");
        }
    }

    @Override
    public ResponseUtil updateScheduleStatus(int id, String status) {

        DeliveryScheduleEntity scheduleEntity = scheduleRepo.findByScheduleId(id);
        if (scheduleEntity == null) {
            return new ResponseUtil(404, "Schedule not found with ID: " + id, null);
        }
        try {
            scheduleEntity.setStatus(DeliveryScheduleEntity.ScheduleStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            return new ResponseUtil(400, "Invalid status value: " + status, null);
        }
        scheduleRepo.save(scheduleEntity);
        return new ResponseUtil(200, "Schedule status updated successfully", null);
    }


    @Override
    public ResponseUtil updateSchedule(int id, ScheduleReqDTO scheduleReqDTO) {
        DeliveryScheduleEntity scheduleEntity = scheduleRepo.findByScheduleId(id);
        if (scheduleEntity != null) {
            scheduleEntity.setDeliveryDate(scheduleReqDTO.getDeliveryDate());
            scheduleEntity.setDeliveryTime(scheduleReqDTO.getDeliveryTime());
            try {
                scheduleEntity.setStatus(DeliveryScheduleEntity.ScheduleStatus.valueOf(scheduleReqDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                return new ResponseUtil(400, "Invalid status value: " + scheduleReqDTO.getStatus(), null);
            }
            scheduleRepo.save(scheduleEntity);

            List<DeliveryStockEntity> existingStockList = stockRepo.findByScheduleId(id);
            Set<Integer> requestGasIds = scheduleReqDTO.getStockList().stream()
                    .map(StockReqDTO::getGasId)
                    .collect(Collectors.toSet());

            for (DeliveryStockEntity stock : existingStockList) {
                if (!requestGasIds.contains(stock.getGas().getGasId())) {
                    stockRepo.delete(stock);
                }
            }
            List<DeliveryStockEntity> stockEntities = new ArrayList<>();
            if (scheduleReqDTO.getStockList() != null && !scheduleReqDTO.getStockList().isEmpty()) {
                for (StockReqDTO stockDTO : scheduleReqDTO.getStockList()) {
                    DeliveryStockEntity deliveryStock = stockRepo.findByScheduleIdAndGasId(id, stockDTO.getGasId());
                    if (deliveryStock == null) {
                        deliveryStock = new DeliveryStockEntity();
                    }
                    Optional<GasEntity> optionalGas = gasRepo.findById(stockDTO.getGasId());
                    if (optionalGas.isEmpty()) {
                        return new ResponseUtil(404, "Gas not found with ID: " + stockDTO.getGasId(), null);
                    }
                    deliveryStock.setSchedule(scheduleEntity);
                    deliveryStock.setGas(optionalGas.get());
                    deliveryStock.setQty(stockDTO.getQuantity());
                    stockEntities.add(deliveryStock);
                }
                List<DeliveryStockEntity> stockList = stockRepo.saveAll(stockEntities);
                if (stockList != null) {
                    return new ResponseUtil(200, "Schedule Updated Successfully", null);
                } else {
                    throw new RuntimeException("Failed to Update delivery stock");
                }
            } else {
                throw new RuntimeException("Failded to update Schedule");
            }
        } else {
            return new ResponseUtil(404, "Schedule not found with ID: "+id, null);
        }
    }

    @Override
    public ResponseUtil deleteSchedule(int id) {
        DeliveryScheduleEntity scheduleEntity = scheduleRepo.findByScheduleId(id);
        if (scheduleEntity != null){
            scheduleRepo.delete(scheduleEntity);

            List<DeliveryStockEntity> stockEntity = stockRepo.findByScheduleId(id);
            for (DeliveryStockEntity stock : stockEntity) {
                    stockRepo.delete(stock);
            }
        return new ResponseUtil(200, "Schedule Deleted Successfully", null);

        }else{
           throw new RuntimeException("schedule not found with ID: "+id);
        }
    }

}
