package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/invoice")
public class InvoiceController {
    private final InvoiceClient invoiceClient;
    private final QueryClient queryClient;
    private final PackageClient packageClient;
    private final ClientClient clientClient;
    private final OrderClient orderClient;
    private final ProductClient productClient;

    @GetMapping("/show/{id}")
    public String invoiceCreateFromOrder(@PathVariable(name = "id") Long id, Model model) {
        Long lastInvoiceNumber = invoiceClient.findLastInvoiceNumberStartingWith();
        List<OrderProductDTO> orderProductDTOS = queryClient.getOrderProductsByOrderId(id);
        List<PackageDTO> packageDTOS = packageClient.getAllPackages();
        List<ClientDTO> clientDTOS = clientClient.getAllClients();
        OrderDTO orderDTO = orderClient.getOrderById(id);
        List<ProductDTO> productDTOS = productClient.getAllProducts();
        InvoiceDTO invoiceDTO=new InvoiceDTO();
        model.addAttribute("invoiceDTO", invoiceDTO);
        model.addAttribute("lastInvoiceNumber", lastInvoiceNumber);
        model.addAttribute("productDTOS", productDTOS);
        model.addAttribute("orderDTO", orderDTO);
        model.addAttribute("clientDTOS", clientDTOS);
        model.addAttribute("packageDTOS", packageDTOS);
        model.addAttribute("orderProductDTOS", orderProductDTOS);
        return "Query/show";
    }
    @PostMapping("/submit")
    public ModelAndView submitInvoice(@RequestParam("paymentMethod") boolean paymentMethod,
                                      @RequestParam("dateInput") LocalDate paymentDateStr,
                                      @RequestParam("paymentAmount") BigDecimal paymentAmountStr) {
        InvoiceDTO invoiceDTO=new InvoiceDTO();
        invoiceDTO.setTotalPrice(paymentAmountStr);
        invoiceDTO.setDeadline(paymentDateStr);
        invoiceDTO.setCashPayment(paymentMethod);
        for (int i = 0; i <5 ; i++) {
            System.err.println("paymentMethod: "+paymentMethod+"/dateInput:"+paymentDateStr+"/paymentAmountInput:"+paymentAmountStr);
            System.err.println(invoiceDTO);
        }
        return new ModelAndView("redirect:/show");
    }

}
