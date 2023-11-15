package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.OrderProductDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-orderProduct", url = "http://localhost:8088/api/v1/orderProduct")
public interface OrderProductClient {
    @GetMapping()
    List<OrderProductDTO> getAllOrderProducts(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    OrderProductDTO getOrderProductById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    OrderProductDTO createOrderProduct(@Valid @RequestBody OrderProductDTO orderProductDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    void updateOrderProduct(@PathVariable("id") Long id, @Valid @RequestBody OrderProductDTO orderProductDTO, @RequestHeader("Authorization") String auth) ;

    @DeleteMapping("/{id}")
    String deleteOrderProductById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

    @GetMapping("/lessening")
    ResponseEntity<Boolean> findInvoiceOrderProductsByInvoiceIdLessening(@RequestParam Long invoiceId, @RequestHeader("Authorization") String auth);

}
