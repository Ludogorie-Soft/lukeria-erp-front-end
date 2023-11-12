package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/monthlyOrder")
public class MonthlyOrderController {
    private final MonthlyOrderClient monthlyOrderClient;
    private final MonthlyOrderProductClient monthlyOrderProductClient;
    private final PackageClient packageClient;
    private final InvoiceClient invoiceClient;
    private final InvoiceOrderProductClient invoiceOrderProductClient;
    private final OrderProductClient orderProductClient;
    private final ClientClient clientClient;
    private final OrderClient orderClient;
    private static final String ORDERTXT = "monthlyOrder";
    private static final String REDIRECTTXT = "redirect:/monthlyOrder/show";
    private final ImageClient imageService;

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    @GetMapping("/create")
    String createOrder(Model model) {
        MonthlyOrderDTO monthlyOrderDTO = new MonthlyOrderDTO();
        List<ClientDTO> clientDTOS = clientClient.getAllClients();
        model.addAttribute("clients", clientDTOS);
        model.addAttribute(ORDERTXT, monthlyOrderDTO);
        return "MonthlyOrder/create";
    }

    @PostMapping("/submit")
    public ModelAndView submitOrder(@ModelAttribute("monthlyOrder") MonthlyOrderDTO orderDTO) {
        monthlyOrderClient.createMonthlyOrder(orderDTO);
        return new ModelAndView("redirect:/monthlyOrder/addProduct");
    }

    @GetMapping("/editOrder/{id}")
    String editMonthlyOrder(@PathVariable(name = "id") Long id, Model model) {
        MonthlyOrderDTO existingOrder = monthlyOrderClient.getMonthlyOrderById(id);
        List<ClientDTO> clientDTOS = clientClient.getAllClients();
        model.addAttribute("clients", clientDTOS);
        model.addAttribute("order", existingOrder);
        return "MonthlyOrder/edit";
    }

    @GetMapping("/edit/{id}")
    ModelAndView editOrder(@PathVariable(name = "id") Long id, MonthlyOrderDTO orderDTO) {
        monthlyOrderClient.updateMonthlyOrder(id, orderDTO);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/addProduct")
    String createMonthlyOrderProduct(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        MonthlyOrderProductDTO orderProduct = new MonthlyOrderProductDTO();
        MonthlyOrderDTO orderDTO = monthlyOrderClient.getMonthlyOrderById(monthlyOrderClient.findFirstByOrderByIdDesc().getId());
        List<MonthlyOrderProductDTO> orderProductDTOS = monthlyOrderProductClient.getAllMonthlyProductOrders().stream().filter(order -> Objects.equals(order.getMonthlyOrderId(), orderDTO.getId())).toList();
        List<Long> packageDTOIds = orderProductDTOS.stream().map(MonthlyOrderProductDTO::getPackageId).toList();
        List<PackageDTO> packageDTOList = packageDTOIds.stream()
                .map(id1 -> packageClient.getPackageById(id1, token))
                .collect(Collectors.toList());
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("orderProducts", orderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("order", orderDTO);
        model.addAttribute("packages", packageClient.getAllPackages(token));
        model.addAttribute("orderProduct", orderProduct);
        return "MonthlyOrder/addProduct";
    }

    @PostMapping("/addProduct/submit")
    public ModelAndView submitMonthlyOrderProduct(@ModelAttribute("orderProduct") MonthlyOrderProductDTO monthlyOrderProductDTO,
                                                  @RequestParam(value = "addAnotherDish", required = false) boolean addAnotherDish, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        monthlyOrderProductDTO.setMonthlyOrderId(monthlyOrderClient.findFirstByOrderByIdDesc().getId());
        monthlyOrderProductDTO.setSentQuantity(0);
        monthlyOrderProductClient.createMonthlyProductOrder(monthlyOrderProductDTO);
        if (addAnotherDish) {
            MonthlyOrderDTO monthlyOrderDTO = monthlyOrderClient.getMonthlyOrderById(monthlyOrderClient.findFirstByOrderByIdDesc().getId());
            model.addAttribute("order", monthlyOrderDTO);
            model.addAttribute("packages", packageClient.getAllPackages(token));
            List<MonthlyOrderProductDTO> monthlyOrderProductDTOS = monthlyOrderProductClient.getAllMonthlyProductOrders().stream().filter(order -> Objects.equals(order.getMonthlyOrderId(), monthlyOrderDTO.getId())).toList();
            List<Long> packageDTOIds = monthlyOrderProductDTOS.stream().map(MonthlyOrderProductDTO::getPackageId).toList();
            List<PackageDTO> packageDTOList = packageDTOIds.stream()
                    .map(id1 -> packageClient.getPackageById(id1, token))
                    .collect(Collectors.toList());
            model.addAttribute("orderProducts", monthlyOrderProductDTOS);
            model.addAttribute("products", packageDTOList);
            model.addAttribute("backendBaseUrl", backendBaseUrl);
            return new ModelAndView("MonthlyOrder/addProduct");
        }
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/addProductToExistingOrder/{orderId}")
    public String addProductToExistingOrder(@PathVariable("orderId") Long orderId, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        MonthlyOrderProductDTO monthlyOrderProductDTO = new MonthlyOrderProductDTO();
        MonthlyOrderDTO orderDTO = monthlyOrderClient.getMonthlyOrderById(orderId);
        List<MonthlyOrderProductDTO> monthlyOrderProductDTOS = monthlyOrderProductClient.getAllMonthlyProductOrders().stream().filter(order -> Objects.equals(order.getMonthlyOrderId(), orderDTO.getId())).toList();
        List<Long> packageDTOIds = monthlyOrderProductDTOS.stream().map(MonthlyOrderProductDTO::getPackageId).toList();
        List<PackageDTO> packageDTOList = packageDTOIds.stream()
                .map(id1 -> packageClient.getPackageById(id1, token))
                .collect(Collectors.toList());
        model.addAttribute("orderProducts", monthlyOrderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("order", orderDTO);
        model.addAttribute("packages", packageClient.getAllPackages(token));
        model.addAttribute("orderProduct", monthlyOrderProductDTO);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        return "MonthlyOrder/addProductToExistingOrder";
    }

    @PostMapping("/submitExistingOrder")
    public ModelAndView submitExistingOrderProduct(@ModelAttribute("orderProduct") MonthlyOrderProductDTO monthlyOrderProductDTO, Model model,
                                                   @RequestParam("orderId") Long orderId) {
        monthlyOrderProductDTO.setMonthlyOrderId(orderId);
        monthlyOrderProductDTO.setSentQuantity(0);
        monthlyOrderProductClient.createMonthlyProductOrder(monthlyOrderProductDTO);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/show")
    public String index(Model model) {
        List<MonthlyOrderDTO> orders = monthlyOrderClient.getAllMonthlyOrders();
        List<Long> clientIds = orders.stream().map(MonthlyOrderDTO::getClientId).toList();
        List<ClientDTO> clients = new java.util.ArrayList<>(clientIds.stream().map(clientClient::getClientById).toList());
        Collections.reverse(orders);
        Collections.reverse(clients);
        model.addAttribute("orders", orders);
        model.addAttribute("clients", clients);
        return "MonthlyOrder/show";
    }

    @GetMapping("/orderDetails/{orderId}")
    public String orderDetails(@PathVariable("orderId") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        MonthlyOrderDTO monthlyOrderDTO = monthlyOrderClient.getMonthlyOrderById(id);
        List<MonthlyOrderProductDTO> monthlyOrderProductDTOS = new ArrayList<>();
        List<InvoiceOrderProductDTO> invoiceOrderProductDTOS = invoiceOrderProductClient.getAllInvoiceOrderProduct();
        for (MonthlyOrderProductDTO order : monthlyOrderProductClient.getAllMonthlyProductOrders()) {
            if (Objects.equals(order.getMonthlyOrderId(), id)) {
                for (InvoiceOrderProductDTO invoiceOrderProductDTO1 : invoiceOrderProductDTOS) {
                    InvoiceDTO invoiceDTO = invoiceClient.getInvoiceById(invoiceOrderProductDTO1.getInvoiceId());
                    if ((invoiceDTO.getInvoiceDate().isBefore(monthlyOrderDTO.getEndDate()) || invoiceDTO.getInvoiceDate().equals(monthlyOrderDTO.getEndDate())) && (invoiceDTO.getInvoiceDate().isAfter(monthlyOrderDTO.getStartDate()) || invoiceDTO.getInvoiceDate().equals(monthlyOrderDTO.getStartDate()))) {
                        Integer sent = 0;
                        order.setSentQuantity(sent);
                        System.out.println(monthlyOrderProductClient.getAllMonthlyProductOrders());
                        for (OrderProductDTO orderProduct : orderProductClient.getAllOrderProducts()) {
                            if (Objects.equals(orderClient.getOrderById(orderProduct.getOrderId()).getClientId(), monthlyOrderDTO.getClientId())) {
                                if (Objects.equals(order.getPackageId(), orderProduct.getPackageId())) {
                                    sent += orderProduct.getNumber();
                                }
                            }
                        }
                        order.setSentQuantity(order.getSentQuantity() + sent);
                        monthlyOrderProductClient.updateMonthlyProductOrder(order.getId(), order);
                    }
                }
                monthlyOrderProductDTOS.add(order);
            }
        }
        List<Long> packageDTOIds = monthlyOrderProductDTOS.stream().map(MonthlyOrderProductDTO::getPackageId).toList();
        List<PackageDTO> packageDTOList = packageDTOIds.stream()
                .map(id1 -> packageClient.getPackageById(id1, token))
                .collect(Collectors.toList());
        model.addAttribute("client", clientClient.getClientById(monthlyOrderDTO.getClientId()));
        model.addAttribute("orderProducts", monthlyOrderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("order", monthlyOrderDTO);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        return "MonthlyOrder/orderDetails";
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteOrderById(@PathVariable("id") Long id, Model model) {
        monthlyOrderClient.deleteMonthlyOrder(id);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/deleteMonthlyOrderProduct/delete/{id}")
    ModelAndView deleteMonthlyOrderProduct(@PathVariable("id") Long id, Model model) {
        monthlyOrderProductClient.deleteMonthlyProductOrder(id);
        return new ModelAndView(REDIRECTTXT);
    }
}