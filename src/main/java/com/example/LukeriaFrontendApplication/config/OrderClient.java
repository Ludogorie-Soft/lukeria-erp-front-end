package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.OrderDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-order", url = "${backend.base-url}/order")
public interface OrderClient {
    @GetMapping()
    List<OrderDTO> getAllOrders(@RequestHeader("Authorization") String auth);

    @GetMapping("/findLastOrder")
    OrderDTO findFirstByOrderByIdDesc(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    OrderDTO getOrderById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    void updateOrder(@PathVariable("id") Long id, @Valid @RequestBody OrderDTO orderDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deleteOrderById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

//    @GetMapping("/client/orders")
//    ResponseEntity<List<OrderDTO>> getOrdersForClient(@RequestParam(name = "id") Long id, @RequestHeader("Authorization") String auth);

}
