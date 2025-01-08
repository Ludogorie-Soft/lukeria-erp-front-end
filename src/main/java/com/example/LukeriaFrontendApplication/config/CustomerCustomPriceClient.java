package com.example.LukeriaFrontendApplication.config;


import com.example.LukeriaFrontendApplication.dtos.CustomerCustomPriceDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "lukeria-erp-customerCustomPrice", url = "${backend.base-url}/customerCustomPrice")
public interface CustomerCustomPriceClient {
    @GetMapping
    List<CustomerCustomPriceDTO> getAllCustomPrices(@RequestHeader("Authorization") String auth);

    @PostMapping
    CustomerCustomPriceDTO createCustomPriceForCustomer(@Valid @RequestBody CustomerCustomPriceDTO customerCustomPriceDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    CustomerCustomPriceDTO updateCustomPrice(@Valid @RequestBody CustomerCustomPriceDTO customerCustomPriceDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/")
    CustomerCustomPriceDTO deleteCustomPrice(@RequestParam(name = "clientId") Long clientId, @RequestParam(name = "productId") Long productId, @RequestHeader("Authorization") String auth);

    @GetMapping("/allForClient/{id}")
    List<CustomerCustomPriceDTO> allProductsWithAndWithoutCustomPrice(@PathVariable(name = "id") Long clientId, @RequestHeader("Authorization") String auth);

    @GetMapping("/findByClientAndProduct")
    CustomerCustomPriceDTO customPriceByClientAndProduct(@RequestParam Long clientId, @RequestParam Long productId, @RequestHeader("Authorization") String auth);

    }


