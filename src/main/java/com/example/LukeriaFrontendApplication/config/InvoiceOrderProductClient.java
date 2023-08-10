package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.InvoiceOrderProductDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-invoiceOrderProduct", url = "http://localhost:8088/api/v1/invoiceOrderProduct")
public interface InvoiceOrderProductClient {
    @GetMapping()
    List<InvoiceOrderProductDTO> getAllInvoiceOrderProduct();

    @GetMapping("/{id}")
    InvoiceOrderProductDTO getInvoiceOrderProductById(@PathVariable(name = "id") Long id);

    @PostMapping
    InvoiceOrderProductDTO createInvoiceOrderProduct(@Valid @RequestBody InvoiceOrderProductDTO invoiceOrderProductDTO);

    @PutMapping("/{id}")
    void updateInvoiceOrderProduct(@PathVariable("id") Long id, @Valid @RequestBody  InvoiceOrderProductDTO invoiceOrderProductDTO) ;

    @DeleteMapping("/{id}")
    String deleteInvoiceOrderProductById(@PathVariable("id") Long id);

}
