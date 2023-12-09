package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ClientClient;
import com.example.LukeriaFrontendApplication.config.MonthlyOrderClient;
import com.example.LukeriaFrontendApplication.config.MonthlyOrderProductClient;
import com.example.LukeriaFrontendApplication.config.OrderClient;
import com.example.LukeriaFrontendApplication.dtos.ClientDTO;
import com.example.LukeriaFrontendApplication.dtos.MonthlyOrderDTO;
import com.example.LukeriaFrontendApplication.dtos.OrderDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/order")
public class OrderController {
    private final OrderClient orderClient;
    private final ClientClient clientClient;
    private final MonthlyOrderClient monthlyOrderClient;
    private static final String ORDERTXT = "order";
    private static final String REDIRECTTXT = "redirect:/order/show";

    @GetMapping("/create")
    String createOrder(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        OrderDTO orderDTO = new OrderDTO();
        List<ClientDTO> clientDTOS = clientClient.getAllClients(token);
        model.addAttribute("clients", clientDTOS);
        model.addAttribute(ORDERTXT, orderDTO);
        return "OrderProduct/create";
    }
    @GetMapping("/monthly/create")
    String createOrderMonthly(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        OrderDTO orderDTO = new OrderDTO();
        List<ClientDTO> clientDTOS = new ArrayList<>();
        for (ClientDTO clientDTO: clientClient.getAllClients(token)) {
            for (MonthlyOrderDTO monthlyOrder : monthlyOrderClient.getAllMonthlyOrders(token)) {
                if (Objects.equals(clientDTO.getId(), monthlyOrder.getClientId()) && !monthlyOrder.isInvoiced()) {
                    clientDTOS.add(clientDTO);
                }
            }
        }
        model.addAttribute("clients", clientDTOS);
        model.addAttribute(ORDERTXT, orderDTO);
        return "OrderProduct/create";
    }

    @PostMapping("/submit")
    public ModelAndView submitOrder(@ModelAttribute("order") OrderDTO orderDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        orderClient.createOrder(orderDTO, token);
        return new ModelAndView("redirect:/orderProduct/addProduct");
    }

    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<OrderDTO> orders = orderClient.getAllOrders(token);
        List<Long> clientIds = orders.stream().map(OrderDTO::getClientId).toList();
        List<ClientDTO> clients = new java.util.ArrayList<>(clientIds.stream()
                .map(id1 -> clientClient.getClientById(id1, token))
                .collect(Collectors.toList()));
        Collections.reverse(orders);
        Collections.reverse(clients);
        model.addAttribute("orders", orders);
        model.addAttribute("clients", clients);
        return "OrderProduct/show";
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteOrderById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        orderClient.deleteOrderById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/editOrder/{id}")
    String editOrder(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        OrderDTO existingOrder = orderClient.getOrderById(id, token);
        List<ClientDTO> clientDTOS = clientClient.getAllClients(token);
        model.addAttribute("clients", clientDTOS);
        model.addAttribute(ORDERTXT, existingOrder);
        return "OrderProduct/edit";
    }

    @GetMapping("/edit/{id}")
    ModelAndView editOrder(@PathVariable(name = "id") Long id, OrderDTO orderDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        orderClient.updateOrder(id, orderDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }
}