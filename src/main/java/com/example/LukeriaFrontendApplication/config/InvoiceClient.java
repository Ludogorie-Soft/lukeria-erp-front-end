package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.InvoiceDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-invocie", url = "${backend.base-url}/invoice")
public interface InvoiceClient {

    @GetMapping
    List<InvoiceDTO> getAllInvoices(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    InvoiceDTO getInvoiceById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    InvoiceDTO createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    void updateInvoice(@PathVariable("id") Long id, @Valid @RequestBody InvoiceDTO invoiceDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deleteInvoiceById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

    @GetMapping("/number")
    Long findLastInvoiceNumberStartingWith(@RequestHeader("Authorization") String auth);

    @GetMapping("/number/abroad")
    Long findLastInvoiceNumberStartingWithOne(@RequestHeader("Authorization") String auth);
}
