package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.MonthlyOrderProductDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.*;

import java.util.List;
@FeignClient(name = "lukeria-erp-monthlyOrderProduct", url = "http://localhost:8088/api/v1/monthlyOrderProduct")
public interface MonthlyOrderProductClient {
    @GetMapping
    List<MonthlyOrderProductDTO> getAllMonthlyProductOrders();
    @GetMapping("/{id}")
    MonthlyOrderProductDTO getMonthlyOrderProductById(@PathVariable(name = "id") Long id);

    @PostMapping
    MonthlyOrderProductDTO createMonthlyProductOrder(@Valid @RequestBody MonthlyOrderProductDTO monthlyOrderProduct);

    @PutMapping("/{id}")
    MonthlyOrderProductDTO updateMonthlyProductOrder(@PathVariable("id") Long id, @Valid @RequestBody MonthlyOrderProductDTO monthlyOrderDTO);

    @DeleteMapping("/{id}")
    String deleteMonthlyProductOrder(@PathVariable("id") Long id);
}
