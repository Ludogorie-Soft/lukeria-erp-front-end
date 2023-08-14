package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.InvoiceDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-invocie", url = "http://localhost:8088/api/v1/invoice")
public interface InvoiceClient {

    @GetMapping
    List<InvoiceDTO> getAllInvoices();

    @GetMapping("/{id}")
    InvoiceDTO getInvoiceById(@PathVariable(name = "id") Long id);

    @PostMapping
    InvoiceDTO createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO);

    @PutMapping("/{id}")
    InvoiceDTO updateInvoice(@PathVariable("id") Long id, @Valid @RequestBody InvoiceDTO invoiceDTO);

    @DeleteMapping("/{id}")
    String deleteInvoiceById(@PathVariable("id") Long id);

    @GetMapping("/number")
    Long findLastInvoiceNumberStartingWith();
}
