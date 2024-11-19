package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.OrderDTO;
import com.example.LukeriaFrontendApplication.dtos.OrderProductDTO;
import com.example.LukeriaFrontendApplication.dtos.OrderWithProductsDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FeignClient(name = "lukeria-erp-orderProduct", url = "${backend.base-url}/orderProduct")
public interface OrderProductClient {
    @GetMapping()
    List<OrderProductDTO> getAllOrderProducts(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    OrderProductDTO getOrderProductById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    OrderProductDTO createOrderProduct(@Valid @RequestBody OrderProductDTO orderProductDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    void updateOrderProduct(@PathVariable("id") Long id, @Valid @RequestBody OrderProductDTO orderProductDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deleteOrderProductById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

    @GetMapping("/lessening")
    ResponseEntity<Boolean> findInvoiceOrderProductsByInvoiceIdLessening(@RequestParam Long invoiceId, @RequestHeader("Authorization") String auth);
    @GetMapping("/order-products-by-orders")
    ResponseEntity<List<OrderWithProductsDTO>> getOrderProductDTOsByOrderDTOs(
            @RequestParam(name = "id") Long id,
            @RequestHeader("Authorization") String auth);
}
