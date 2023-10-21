package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private static final String ORDERPRODUCT = "orderProductDTOS";
    private static final String PACKAGE = "packageDTOS";
    private static final String REGEX = "[\\[\\]]";

    @GetMapping("/show/{id}")
    public String invoiceCreateFromOrder(@PathVariable(name = "id") Long id, Model model) {
        Long lastInvoiceNumber = 0L;
        List<OrderProductDTO> orderProductDTOS = queryClient.getOrderProductsByOrderId(id);
        OrderDTO order = orderClient.getOrderById(orderProductDTOS.get(0).getOrderId());
        ClientDTO client = clientClient.getClientById(order.getClientId()) ;
        if(client.isBulgarianClient()){
            lastInvoiceNumber = invoiceClient.findLastInvoiceNumberStartingWith();
        } else{
            lastInvoiceNumber = invoiceClient.findLastInvoiceNumberStartingWithOne();
        }
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
        model.addAttribute(PACKAGE, packageDTOS);
        model.addAttribute(ORDERPRODUCT, orderProductDTOS);
        return "Query/show";
    }

    @GetMapping("/showId/{id}")
    public String invoiceShow(@PathVariable(name = "id") Long id, Model model, HttpSession session) {
        Long lastInvoiceNumber = invoiceClient.findLastInvoiceNumberStartingWith();
        Long orderId = 0L;
        for (InvoiceOrderProductDTO invoiceOrderProductDTO: invoiceOrderProductClient.getAllInvoiceOrderProduct()) {
            if(invoiceOrderProductDTO.getInvoiceId() == id){
                OrderProductDTO orderProductDTO = orderProductClient.getOrderProductById(invoiceOrderProductDTO.getOrderProductId());
                orderId = orderProductDTO.getOrderId();
                break;
            }
        }
        List<OrderProductDTO> orderProductDTOS = queryClient.getOrderProductsByOrderId(orderId);
        List<PackageDTO> packageDTOS = packageClient.getAllPackages();
        List<ClientDTO> clientDTOS = clientClient.getAllClients();
        OrderDTO orderDTO = orderClient.getOrderById(orderId);
        List<ProductDTO> productDTOS = productClient.getAllProducts();
        InvoiceDTO invoiceDTO = invoiceClient.getInvoiceById(id);
        ClientDTO clientDTO = clientClient.getClientById(orderDTO.getClientId());
        if (invoiceDTO.getInvoiceNumber() >= 2000000000) {
            session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, new Locale("bg"));
            model.addAttribute("clientName", clientDTO.getBusinessName());
            model.addAttribute("clientAddress", clientDTO.getAddress());
        } else {
            session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
            model.addAttribute("clientName", clientDTO.getEnglishBusinessName());
            model.addAttribute("clientAddress", clientDTO.getEnglishAddress());
        }
        model.addAttribute("InvoiceId", id);
        model.addAttribute("invoiceDTO", invoiceDTO);
        model.addAttribute("lastInvoiceNumber", lastInvoiceNumber);
        model.addAttribute("productDTOS", productDTOS);
        model.addAttribute("orderDTO", orderDTO);
        model.addAttribute("clientDTOS", clientDTOS);
        model.addAttribute(PACKAGE, packageDTOS);
        model.addAttribute(ORDERPRODUCT, orderProductDTOS);
        return "Invoice/showId";
    }

    @GetMapping("/showAllInvoices")
    public String showAllInvoices(Model model) {
        List<InvoiceDTO> invoiceDTOS = invoiceClient.getAllInvoices();
        model.addAttribute("invoiceDTOS", invoiceDTOS);
        return "Invoice/showAllInvoices";
    }

    @PostMapping("/submit")
    public ModelAndView submitInvoice(@RequestParam("paymentMethod") boolean paymentMethod,
                                      @RequestParam("dateInput") LocalDate paymentDateStr,
                                      @RequestParam("paymentAmount") BigDecimal paymentAmountStr,
                                      @RequestParam("lastInvoiceNumber") Long invoiceNumber,
                                      @RequestParam("currentDate") String currentDate,
                                      @RequestParam("orderProductIds") List<Long> orderProductIds,
                                      @RequestParam("quantityInputList") List<String> quantityInput,
                                      @RequestParam("priceInputList") List<String> priceInputList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate currentDateSte = LocalDate.parse(currentDate, formatter);

        List<Integer> quantityInputIntList = quantityInput.stream()
                .map(s -> s.replaceAll(REGEX, ""))
                .map(Integer::parseInt).toList();

        List<BigDecimal> priceInputBigDecimalList = priceInputList.stream()
                .map(s -> s.replaceAll(REGEX, ""))
                .map(BigDecimal::new).toList();

        InvoiceDTO invoiceDTO = new InvoiceDTO();

        invoiceDTO.setInvoiceNumber(invoiceNumber);
        invoiceDTO.setInvoiceDate(currentDateSte);
        invoiceDTO.setTotalPrice(paymentAmountStr);
        invoiceDTO.setDeadline(paymentDateStr);
        invoiceDTO.setCashPayment(paymentMethod);

        InvoiceOrderProductConfigDTO invoiceOrderProductConfigDTO = new InvoiceOrderProductConfigDTO();
        InvoiceDTO createdInvoice = invoiceClient.createInvoice(invoiceDTO);

        invoiceOrderProductConfigDTO.setInvoiceId(createdInvoice.getId());
        invoiceOrderProductConfigDTO.setOrderProductIds(orderProductIds);
        invoiceOrderProductConfigDTO.setQuantityInputIntList(quantityInputIntList);
        invoiceOrderProductConfigDTO.setPriceInputBigDecimalList(priceInputBigDecimalList);

        invoiceOrderProductClient.createInvoiceOrderProductWhitIdsList(invoiceOrderProductConfigDTO);
        return new ModelAndView("redirect:/invoice/showId/" + (createdInvoice.getId()));
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
        model.addAttribute(PACKAGE, packageDTOS);
        model.addAttribute(ORDERPRODUCT, orderProductDTOS);
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
}
