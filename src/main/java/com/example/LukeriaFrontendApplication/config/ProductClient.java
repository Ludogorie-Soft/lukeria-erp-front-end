package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-product", url = "http://localhost:8088/api/v1/product")

public interface ProductClient {
    @GetMapping
    List<ProductDTO> getAllProducts();

    @GetMapping("/{id}")
    ProductDTO getProductById(@PathVariable(name = "id") Long id);

    @PostMapping
    ProductDTO createProduct(@Valid @RequestBody ProductDTO productDTO);

    @PutMapping("/{id}")
    ProductDTO updateProduct(@PathVariable("id") Long id, @Valid @RequestBody ProductDTO productDTO);

    @DeleteMapping("/{id}")
    String deleteProductById(@PathVariable("id") Long id);
    @PostMapping("/produce")
    ResponseEntity<ProductDTO> produceProduct(@RequestParam("productId") Long productId, @RequestParam("producedQuantity") int producedQuantity);
}
