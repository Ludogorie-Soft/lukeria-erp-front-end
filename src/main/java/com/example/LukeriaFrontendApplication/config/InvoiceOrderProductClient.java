package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.InvoiceOrderProductConfigDTO;
import com.example.LukeriaFrontendApplication.dtos.InvoiceOrderProductDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-invoiceOrderProduct", url = "http://localhost:8088/api/v1/invoiceOrderProduct")
public interface InvoiceOrderProductClient {
    @GetMapping()
    List<InvoiceOrderProductDTO> getAllInvoiceOrderProduct(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    InvoiceOrderProductDTO getInvoiceOrderProductById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    InvoiceOrderProductDTO createInvoiceOrderProduct(@Valid @RequestBody InvoiceOrderProductDTO invoiceOrderProductDTO, @RequestHeader("Authorization") String auth);

    @PostMapping("/withIds")
    ResponseEntity<String> createInvoiceOrderProductWhitIdsList(InvoiceOrderProductConfigDTO configDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    void updateInvoiceOrderProduct(@PathVariable("id") Long id, @Valid @RequestBody InvoiceOrderProductDTO invoiceOrderProductDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deleteInvoiceOrderProductById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

}
