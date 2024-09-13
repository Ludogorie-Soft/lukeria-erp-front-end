package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.CustomerCustomPriceDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "lukeria-erp-customerCustomPrice", url = "${backend.base-url}/customerCustomPrice")
public interface CustomerCustomPriceClient {
    @GetMapping
    List<CustomerCustomPriceDTO> getAllCustomPrices(@RequestHeader("Authorization") String auth);

    @PostMapping
    CustomerCustomPriceDTO createCustomPriceForCustomer(@Valid @RequestBody CustomerCustomPriceDTO customerCustomPriceDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    CustomerCustomPriceDTO updateCustomPrice(@Valid @RequestBody CustomerCustomPriceDTO customerCustomPriceDTO, @PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    CustomerCustomPriceDTO deleteCustomPrice(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

}


