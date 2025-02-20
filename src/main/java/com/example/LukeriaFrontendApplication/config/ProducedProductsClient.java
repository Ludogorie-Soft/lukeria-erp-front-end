package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.ManufacturedProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "lukeria-erp-producedProducts", url = "${backend.base-url}/manufactured-product")

public interface ProducedProductsClient {
    @GetMapping()
    List<ManufacturedProductDTO> getAllManufacturedProducts(@RequestHeader("Authorization") String auth) ;
    }
