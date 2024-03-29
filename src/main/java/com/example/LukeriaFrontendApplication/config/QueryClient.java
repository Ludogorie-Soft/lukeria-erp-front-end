package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.OrderProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "lukeria-erp-invoice", url = "${backend.base-url}/query")

public interface QueryClient {
    @GetMapping("/orderProduct/{id}")
    List<OrderProductDTO> getOrderProductsByOrderId(@PathVariable(name = "id") Long id);
}
