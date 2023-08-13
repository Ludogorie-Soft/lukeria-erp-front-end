package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/invoice")
public class InvoiceController {
    private final QueryClient queryClient;
    private final PackageClient packageClient;
    private final ClientClient clientClient;
    private final OrderClient orderClient;
    private  final ProductClient productClient;

    @GetMapping("/show/{id}")
    public String index(@PathVariable(name = "id") Long id, Model model) {
        Long lastInvoiceNumber=2000000000L;
        List<OrderProductDTO> orderProductDTOS = queryClient.getOrderProductsByOrderId(id);
        List<PackageDTO> packageDTOS = packageClient.getAllPackages();
        List<ClientDTO> clientDTOS = clientClient.getAllClients();
        OrderDTO orderDTO = orderClient.getOrderById(id);
        List<ProductDTO> productDTOS=productClient.getAllProducts();
        model.addAttribute("lastInvoiceNumber",lastInvoiceNumber);
        model.addAttribute("productDTOS",productDTOS);
        model.addAttribute("orderDTO", orderDTO);
        model.addAttribute("clientDTOS", clientDTOS);
        model.addAttribute("packageDTOS", packageDTOS);
        model.addAttribute("orderProductDTOS", orderProductDTOS);
        return "Query/show";
    }

}
