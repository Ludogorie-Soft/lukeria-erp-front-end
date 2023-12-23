package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.*;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/invoice")
public class InvoiceController {
    private static final String ORDERPRODUCT = "orderProductDTOS";
    private static final String PACKAGE = "packageDTOS";
    private static final String REGEX = "[\\[\\]]";
    private final InvoiceClient invoiceClient;
    private final QueryClient queryClient;
    private final PackageClient packageClient;
    private final ClientClient clientClient;
    private final OrderClient orderClient;
    private final ProductClient productClient;
    private final InvoiceOrderProductClient invoiceOrderProductClient;
    private final OrderProductClient orderProductClient;

    @GetMapping("/show/{id}")
    public String invoiceCreateFromOrder(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        Long lastInvoiceNumber = 0L;
        List<OrderProductDTO> orderProductDTOS = queryClient.getOrderProductsByOrderId(id);
        OrderDTO order = orderClient.getOrderById(orderProductDTOS.get(0).getOrderId(), token);
        ClientDTO client = clientClient.getClientById(order.getClientId(), token);
        if (client.isBulgarianClient()) {
            lastInvoiceNumber = invoiceClient.findLastInvoiceNumberStartingWith(token);
        } else {
            lastInvoiceNumber = invoiceClient.findLastInvoiceNumberStartingWithOne(token);
        }
        List<PackageDTO> packageDTOS = packageClient.getAllPackages(token);
        List<ClientDTO> clientDTOS = clientClient.getAllClients(token);
        OrderDTO orderDTO = orderClient.getOrderById(id, token);
        List<ProductDTO> productDTOS = productClient.getAllProducts(token);
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        model.addAttribute("bankAccount", "");
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
    public String invoiceShow(@PathVariable(name = "id") Long id, Model model, HttpSession session, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        Long lastInvoiceNumber = invoiceClient.findLastInvoiceNumberStartingWith(token);
        Long orderId = 0L;
        for (InvoiceOrderProductDTO invoiceOrderProductDTO : invoiceOrderProductClient.getAllInvoiceOrderProduct(token)) {
            if (invoiceOrderProductDTO.getInvoiceId() == id) {
                OrderProductDTO orderProductDTO = orderProductClient.getOrderProductById(invoiceOrderProductDTO.getOrderProductId(), token);
                orderId = orderProductDTO.getOrderId();
                break;
            }
        }
        List<OrderProductDTO> orderProductDTOS = queryClient.getOrderProductsByOrderId(orderId);
        List<PackageDTO> packageDTOS = packageClient.getAllPackages(token);
        List<ClientDTO> clientDTOS = clientClient.getAllClients(token);
        OrderDTO orderDTO = orderClient.getOrderById(orderId, token);
        List<ProductDTO> productDTOS = productClient.getAllProducts(token);
        InvoiceDTO invoiceDTO = invoiceClient.getInvoiceById(id, token);
        ClientDTO clientDTO = clientClient.getClientById(orderDTO.getClientId(), token);
        if (invoiceDTO.getInvoiceNumber() >= 2000000000) {
            session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, new Locale("bg"));
            model.addAttribute("clientName", clientDTO.getBusinessName());
            model.addAttribute("clientAddress", clientDTO.getAddress());
            model.addAttribute("clientMOL", clientDTO.getMol());
        } else {
            session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
            model.addAttribute("clientName", clientDTO.getEnglishBusinessName());
            model.addAttribute("clientAddress", clientDTO.getEnglishAddress());
            model.addAttribute("clientMOL", clientDTO.getEnglishMol());
        }
        orderDTO.setInvoiced(true);
        orderClient.updateOrder(orderId, orderDTO, token);
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
    public String showAllInvoices(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<InvoiceDTO> invoiceDTOS = invoiceClient.getAllInvoices(token);
        Collections.reverse(invoiceDTOS);
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
                                      @RequestParam("priceInputList") List<String> priceInputList,
                                      @RequestParam("bankAccount") String bankAccount,
                                      HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
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
        if (invoiceDTO.getInvoiceNumber() >= 2000000000) {
            invoiceDTO.setBankAccount(bankAccount);
        } else if (bankAccount.equals("BG56-UNCR-7000-1523215088 УНИКРЕДИТ БУЛБАНК АД")) {
            invoiceDTO.setBankAccount("BG56-UNCR-7000-1523215088 UNICREDIT BULBANK AD");
        } else if (bankAccount.equals("BG84-BPBI-7943-1076363002 ЮРОБАНК БЪЛГАРИЯ АД")) {
            invoiceDTO.setBankAccount("BG84-BPBI-7943-1076363002 EUROBANK BULGARIA AD");
        } else if (bankAccount.equals("BG06-DEMI-9240-1000326433 ТЪРГОВСКА БАНКА Д АД")) {
            invoiceDTO.setBankAccount("BG06-DEMI-9240-1000326433 COMMERCIAL BANK D AD");
        }
        InvoiceOrderProductConfigDTO invoiceOrderProductConfigDTO = new InvoiceOrderProductConfigDTO();
        InvoiceDTO createdInvoice = invoiceClient.createInvoice(invoiceDTO, token);

        invoiceOrderProductConfigDTO.setInvoiceId(createdInvoice.getId());
        invoiceOrderProductConfigDTO.setOrderProductIds(orderProductIds);
        invoiceOrderProductConfigDTO.setQuantityInputIntList(quantityInputIntList);
        invoiceOrderProductConfigDTO.setPriceInputBigDecimalList(priceInputBigDecimalList);

        invoiceOrderProductClient.createInvoiceOrderProductWhitIdsList(invoiceOrderProductConfigDTO, token);
        orderProductClient.findInvoiceOrderProductsByInvoiceIdLessening(createdInvoice.getId(), token);
        return new ModelAndView("redirect:/invoice/showId/" + (createdInvoice.getId()));
    }

    @GetMapping("/certificate/{id}")
    public String certificate(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        InvoiceDTO invoiceDTO = invoiceClient.getInvoiceById(id, token);
        OrderProductDTO orderProductDTO = null;
        for (InvoiceOrderProductDTO order : invoiceOrderProductClient.getAllInvoiceOrderProduct(token)) {
            if (Objects.equals(order.getInvoiceId(), id)) {
                orderProductDTO = orderProductClient.getOrderProductById(order.getOrderProductId(), token);
            }
        }
        if (orderProductDTO != null) {
            OrderDTO orderDTO = orderClient.getOrderById(orderProductDTO.getOrderId(), token);
            ClientDTO clientDTO = clientClient.getClientById(orderDTO.getClientId(), token);
            model.addAttribute("client", clientDTO);
        }
        model.addAttribute("date", invoiceDTO.getInvoiceDate());
        model.addAttribute("invoiceNumber", invoiceDTO.getInvoiceNumber());
        return "Certificate/Certificate";
    }

    @GetMapping("/confirmation/{id}")
    public String confirmation(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        InvoiceDTO invoiceDTO = invoiceClient.getInvoiceById(id, token);
        List<OrderProductDTO> orderProductDTOS = new ArrayList<>();
        for (InvoiceOrderProductDTO invoiceOrderProductDTO : invoiceOrderProductClient.getAllInvoiceOrderProduct(token)) {
            if (Objects.equals(invoiceOrderProductDTO.getInvoiceId(), id)) {
                OrderProductDTO orderProductDTO = orderProductClient.getOrderProductById(invoiceOrderProductDTO.getOrderProductId(), token);
                orderProductDTOS.add(orderProductDTO);
            }
        }
        List<PackageDTO> packageDTOS = new ArrayList<>();
        for (OrderProductDTO order : orderProductDTOS) {
            packageDTOS.add(packageClient.getPackageById(order.getPackageId(), token));
        }
        model.addAttribute(PACKAGE, packageDTOS);
        model.addAttribute(ORDERPRODUCT, orderProductDTOS);
        model.addAttribute("date", invoiceDTO.getInvoiceDate());
        model.addAttribute("invoiceNumber", invoiceDTO.getInvoiceNumber());
        return "Confirmation/confirmation";
    }

    @GetMapping("/certificate/show")
    public String certificateShow(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<InvoiceDTO> invoiceDTOS = invoiceClient.getAllInvoices(token);
        Collections.reverse(invoiceDTOS);
        model.addAttribute("invoices", invoiceDTOS);
        return "Certificate/show";
    }

    @GetMapping("/confirmation/show")
    public String confirmationShow(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<InvoiceDTO> invoiceDTOS = invoiceClient.getAllInvoices(token);
        List<InvoiceDTO> invoicesAbroad = new ArrayList<>();
        for (InvoiceDTO invoice : invoiceDTOS) {
            if (invoice.getInvoiceNumber() < 2000000000) {
                invoicesAbroad.add(invoice);
            }
        }
        Collections.reverse(invoicesAbroad);
        model.addAttribute("invoices", invoicesAbroad);
        return "Confirmation/show";
    }
}
