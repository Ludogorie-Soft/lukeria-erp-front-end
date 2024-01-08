package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.MonthlyOrderProductDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-monthlyOrderProduct", url = "${backend.base-url}/monthlyOrderProduct")
public interface MonthlyOrderProductClient {
    @GetMapping
    List<MonthlyOrderProductDTO> getAllMonthlyProductOrders(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    MonthlyOrderProductDTO getMonthlyOrderProductById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    MonthlyOrderProductDTO createMonthlyProductOrder(@Valid @RequestBody MonthlyOrderProductDTO monthlyOrderProduct, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    MonthlyOrderProductDTO updateMonthlyProductOrder(@PathVariable("id") Long id, @Valid @RequestBody MonthlyOrderProductDTO monthlyOrderDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deleteMonthlyProductOrder(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);
}
