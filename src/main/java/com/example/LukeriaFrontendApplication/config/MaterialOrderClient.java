package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.MaterialOrderDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-material-order", url = "http://localhost:8088/api/v1/material-order")
public interface MaterialOrderClient {
    @GetMapping
    List<MaterialOrderDTO> getAllMaterialOrders(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    MaterialOrderDTO getMaterialOrderById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    MaterialOrderDTO createMaterialOrder(@Valid @RequestBody MaterialOrderDTO materialOrderDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    MaterialOrderDTO updateMaterialOrder(@PathVariable("id") Long id, @Valid @RequestBody MaterialOrderDTO materialOrderDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deleteMaterialOrderById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

    @GetMapping("products/{id}")
    List<MaterialOrderDTO> getAllProductsByOrderId(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @GetMapping("/all-missing-materials")
    List<MaterialOrderDTO> allAvailableProducts(@RequestHeader("Authorization") String auth);
}
