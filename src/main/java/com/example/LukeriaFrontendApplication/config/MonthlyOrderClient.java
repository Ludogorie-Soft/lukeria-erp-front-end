package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.MonthlyOrderDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-monthlyOrder", url = "${backend.base-url}/monthlyOrder")
public interface MonthlyOrderClient {
    @GetMapping
    List<MonthlyOrderDTO> getAllMonthlyOrders(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    MonthlyOrderDTO getMonthlyOrderById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    MonthlyOrderDTO createMonthlyOrder(@Valid @RequestBody MonthlyOrderDTO monthlyOrderDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    MonthlyOrderDTO updateMonthlyOrder(@PathVariable("id") Long id, @Valid @RequestBody MonthlyOrderDTO monthlyOrderDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deleteMonthlyOrder(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

    @GetMapping("/findLastMonthlyOrder")
    MonthlyOrderDTO findFirstByOrderByIdDesc(@RequestHeader("Authorization") String auth);
}
