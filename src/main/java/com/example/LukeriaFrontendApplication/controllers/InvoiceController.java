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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private final InvoiceOrderProductClient invoiceOrderProductClient;
    private final OrderProductClient orderProductClient;

    @GetMapping("/show/{id}")
    public String invoiceCreateFromOrder(@PathVariable(name = "id") Long id, Model model) {
        Long lastInvoiceNumber = invoiceClient.findLastInvoiceNumberStartingWith();
        List<OrderProductDTO> orderProductDTOS = queryClient.getOrderProductsByOrderId(id);
        List<PackageDTO> packageDTOS = packageClient.getAllPackages();
        List<ClientDTO> clientDTOS = clientClient.getAllClients();
        OrderDTO orderDTO = orderClient.getOrderById(id);
        List<ProductDTO> productDTOS = productClient.getAllProducts();
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        model.addAttribute("invoiceDTO", invoiceDTO);
        model.addAttribute("lastInvoiceNumber", lastInvoiceNumber);
        model.addAttribute("productDTOS", productDTOS);
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
        for (InvoiceOrderProductDTO order : invoiceOrderProductClient.getAllInvoiceOrderProduct()) {
            if (Objects.equals(order.getInvoiceId(), id)) {
                orderProductDTO = orderProductClient.getOrderProductById(order.getOrderProductId());
            }
        }
        if (orderProductDTO != null) {
            OrderDTO orderDTO = orderClient.getOrderById(orderProductDTO.getOrderId());
            ClientDTO clientDTO = clientClient.getClientById(orderDTO.getClientId());
            model.addAttribute("client", clientDTO);
        }
        model.addAttribute("date", invoiceDTO.getInvoiceDate());
        model.addAttribute("invoiceNumber", invoiceDTO.getInvoiceNumber());
        return "Certificate/Certificate";
    }

    @GetMapping("/confirmation/{id}")
    public String confirmation(@PathVariable(name = "id") Long id, Model model) {
        InvoiceDTO invoiceDTO = invoiceClient.getInvoiceById(id);
        List<OrderProductDTO> orderProductDTOS = new ArrayList<>();
        for (InvoiceOrderProductDTO invoiceOrderProductDTO : invoiceOrderProductClient.getAllInvoiceOrderProduct()) {
            if (Objects.equals(invoiceOrderProductDTO.getInvoiceId(), id)) {
                OrderProductDTO orderProductDTO = orderProductClient.getOrderProductById(invoiceOrderProductDTO.getOrderProductId());
                orderProductDTOS.add(orderProductDTO);
            }
        }
        List<PackageDTO> packageDTOS = new ArrayList<>();
        for (OrderProductDTO order : orderProductDTOS) {
            packageDTOS.add(packageClient.getPackageById(order.getPackageId()));
        }
        model.addAttribute("packageDTOS", packageDTOS);
        model.addAttribute("orderProductDTOS", orderProductDTOS);
        model.addAttribute("date", invoiceDTO.getInvoiceDate());
        model.addAttribute("invoiceNumber", invoiceDTO.getInvoiceNumber());
        return "Confirmation/confirmation";
    }

    @GetMapping("/certificate/show")
    public String certificateShow(Model model) {
        List<InvoiceDTO> invoiceDTOS = invoiceClient.getAllInvoices();
        model.addAttribute("invoices", invoiceDTOS);
        return "Certificate/show";
    }

    @GetMapping("/confirmation/show")
    public String confirmationShow(Model model) {
        List<InvoiceDTO> invoiceDTOS = invoiceClient.getAllInvoices();
        model.addAttribute("invoices", invoiceDTOS);
        return "Confirmation/show";
    }

    @PostMapping("/submit")
    public ModelAndView submitInvoice(@RequestParam("paymentMethod") boolean paymentMethod,
                                      @RequestParam("dateInput") LocalDate paymentDateStr,
                                      @RequestParam("paymentAmount") BigDecimal paymentAmountStr,
                                      @RequestParam("invoiceNumber") Long invoiceNumber,
                                      @RequestParam("currentDate") String currentDate,
                                      @RequestParam("orderProductIds") List<Long> orderProductIds) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate currentDateSte = LocalDate.parse(currentDate, formatter);
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceNumber(invoiceNumber);
        invoiceDTO.setInvoiceDate(currentDateSte);
        invoiceDTO.setTotalPrice(paymentAmountStr);
        invoiceDTO.setDeadline(paymentDateStr);
        invoiceDTO.setCashPayment(paymentMethod);
        InvoiceDTO createdInvoice = invoiceClient.createInvoice(invoiceDTO);
        InvoiceOrderProductConfigDTO invoiceOrderProductConfigDTO = new InvoiceOrderProductConfigDTO();
        invoiceOrderProductConfigDTO.setInvoiceId(createdInvoice.getId());
        invoiceOrderProductConfigDTO.setOrderProductIds(orderProductIds);
        invoiceOrderProductClient.createInvoiceOrderProductWhitIdsList(invoiceOrderProductConfigDTO);
        return new ModelAndView("redirect:/show");
    }


}
