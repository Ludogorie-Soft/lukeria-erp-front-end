package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.MonthlyOrderDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-monthlyOrder", url = "http://localhost:8088/api/v1/monthlyOrder")
public interface MonthlyOrderClient {
    @GetMapping
    List<MonthlyOrderDTO> getAllMonthlyOrders();

    @GetMapping("/{id}")
    MonthlyOrderDTO getMonthlyOrderById(@PathVariable(name = "id") Long id);

    @PostMapping
    MonthlyOrderDTO createMonthlyOrder(@Valid @RequestBody MonthlyOrderDTO monthlyOrderDTO);

    @PutMapping("/{id}")
    MonthlyOrderDTO updateMonthlyOrder(@PathVariable("id") Long id, @Valid @RequestBody MonthlyOrderDTO monthlyOrderDTO);

    @DeleteMapping("/{id}")
    String deleteMonthlyOrder(@PathVariable("id") Long id);

    @GetMapping("/findLastMonthlyOrder")
    MonthlyOrderDTO findFirstByOrderByIdDesc();
}
