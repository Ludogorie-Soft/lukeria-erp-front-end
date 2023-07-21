package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.OrderDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-order", url = "http://localhost:8088/api/v1/order")
public interface OrderClient {
    @GetMapping()
    List<OrderDTO> getAllOrders();

    @GetMapping("/{id}")
    OrderDTO getOrderById(@PathVariable(name = "id") Long id);

    @PostMapping
    OrderDTO createOrder(@Valid @RequestBody OrderDTO orderDTO);

    @PutMapping("/{id}")
    void updateOrder(@PathVariable("id") Long id, @Valid @RequestBody OrderDTO orderDTO) ;

    @DeleteMapping("/{id}")
    String deleteOrderById(@PathVariable("id") Long id);

}
