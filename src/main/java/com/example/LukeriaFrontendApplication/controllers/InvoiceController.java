package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/invoice")
public class InvoiceController {
    private final QueryClient queryClient;
    private final PackageClient packageClient;
    private final ClientClient clientClient;
    private final OrderClient orderClient;
    private final InvoiceClient invoiceClient;
    private final OrderProductClient orderProductClient;
    private final InvoiceOrderProduct invoiceOrderProduct;

    @GetMapping("/show/{id}")
    public String index(@PathVariable(name = "id") Long id, Model model) {
        List<OrderProductDTO> orderProductDTOS = queryClient.getOrderProductsByOrderId(id);
        List<PackageDTO> packageDTOS = packageClient.getAllPackages();
        List<ClientDTO> clientDTOS = clientClient.getAllClients();
        OrderDTO orderDTO = orderClient.getOrderById(id);
        model.addAttribute("orderDTO", orderDTO);
        model.addAttribute("clientDTOS", clientDTOS);
        model.addAttribute("packageDTOS", packageDTOS);
        model.addAttribute("orderProductDTOS", orderProductDTOS);
        return "Query/show";
    }

    @GetMapping("/certificate/{id}")
    public String certificate(@PathVariable(name = "id") Long id, Model model) {
        InvoiceDTO invoiceDTO = invoiceClient.getInvoiceById(id);
        OrderProductDTO orderProductDTO = null;
        for (InvoiceOrderProductDTO order: invoiceOrderProduct.getAllInvoiceOrderProduct()) {
            if(Objects.equals(order.getInvoiceId(), id)){
                orderProductDTO = orderProductClient.getOrderProductById(order.getOrderProductId());
            }
        }
        if(orderProductDTO != null) {
            OrderDTO orderDTO = orderClient.getOrderById(orderProductDTO.getOrderId());
            ClientDTO clientDTO = clientClient.getClientById(orderDTO.getClientId());
            model.addAttribute("client", clientDTO);
        }
        model.addAttribute("date", invoiceDTO.getInvoiceDate());
        model.addAttribute("invoiceNumber", invoiceDTO.getInvoiceNumber());
        return "Certificate/Certificate";
    }
    @GetMapping("/certificate/show")
    public String certificateShow(Model model) {
        List<InvoiceDTO> invoiceDTOS = invoiceClient.getAllInvoices();
        model.addAttribute("invoices", invoiceDTOS);
        return "Certificate/show";
    }
}
