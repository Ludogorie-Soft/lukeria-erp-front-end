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
    List<OrderProductDTO> getAllOrderProducts();

    @GetMapping("/{id}")
    OrderProductDTO getOrderProductById(@PathVariable(name = "id") Long id);

    @PostMapping
    OrderProductDTO createOrderProduct(@Valid @RequestBody OrderProductDTO orderProductDTO);

    @PutMapping("/{id}")
    void updateOrderProduct(@PathVariable("id") Long id, @Valid @RequestBody OrderProductDTO orderProductDTO) ;

    @DeleteMapping("/{id}")
    String deleteOrderProductById(@PathVariable("id") Long id);

    @GetMapping("/lessening")
    public ResponseEntity<Boolean> findInvoiceOrderProductsByInvoiceId(@RequestParam Long invoiceId);

}
