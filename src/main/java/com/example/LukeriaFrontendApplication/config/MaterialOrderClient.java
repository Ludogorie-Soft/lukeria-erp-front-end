package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.MaterialOrderDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-client", url = "http://localhost:8088/api/v1/material-order")
public interface MaterialOrderClient {
    @GetMapping
    ResponseEntity<List<MaterialOrderDTO>> getAllMaterialOrders();

    @GetMapping("/{id}")
    ResponseEntity<MaterialOrderDTO> getMaterialOrderById(@PathVariable(name = "id") Long id);

    @PostMapping
    ResponseEntity<MaterialOrderDTO> createMaterialOrder(@Valid @RequestBody MaterialOrderDTO materialOrderDTO);

    @PutMapping("/{id}")
    ResponseEntity<MaterialOrderDTO> updateMaterialOrder(@PathVariable("id") Long id, @Valid @RequestBody MaterialOrderDTO materialOrderDTO);

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteMaterialOrderById(@PathVariable("id") Long id);

    @GetMapping("products/{id}")
    void getAllProductsByOrderId(@PathVariable(name = "id") Long id);
}
