package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ClientClient;
import com.example.LukeriaFrontendApplication.config.OrderClient;
import com.example.LukeriaFrontendApplication.dtos.ClientDTO;
import com.example.LukeriaFrontendApplication.dtos.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order")
public class OrderController {
    private final OrderClient orderClient;
    private final ClientClient clientClient;
    private static final String ORDERTXT = "order";
    private static final String REDIRECTTXT = "redirect:/order/show";

    @GetMapping("/create")
    String createOrder(Model model) {
        OrderDTO orderDTO = new OrderDTO();
        List<ClientDTO> clientDTOS = clientClient.getAllClients();
        model.addAttribute("clients", clientDTOS);
        model.addAttribute(ORDERTXT, orderDTO);
        return "OrderProduct/create";
    }

    @PostMapping("/submit")
    public ModelAndView submitOrder(@ModelAttribute("order") OrderDTO orderDTO) {
        orderClient.createOrder(orderDTO);
        return new ModelAndView("redirect:/orderProduct/addProduct");
    }
    @GetMapping("/show")
    public String index(Model model) {
        List<OrderDTO> orders = orderClient.getAllOrders();
        List<Long> clientIds = orders.stream().map(OrderDTO::getClientId).toList();
        List<ClientDTO> clients = new java.util.ArrayList<>(clientIds.stream().map(clientClient::getClientById).toList());
        Collections.reverse(orders);
        Collections.reverse(clients);
        model.addAttribute("orders", orders);
        model.addAttribute("clients", clients);
        return "OrderProduct/show";
    }
    @PostMapping("/delete/{id}")
    ModelAndView deleteOrderById(@PathVariable("id") Long id, Model model) {
        orderClient.deleteOrderById(id);
        return new ModelAndView(REDIRECTTXT);
    }
    @GetMapping("/editOrder/{id}")
    String editOrder(@PathVariable(name = "id") Long id, Model model) {
        OrderDTO existingOrder = orderClient.getOrderById(id);
        List<ClientDTO> clientDTOS = clientClient.getAllClients();
        model.addAttribute("clients", clientDTOS);
        model.addAttribute(ORDERTXT, existingOrder);
        return "OrderProduct/edit";
    }
    @GetMapping("/edit/{id}")
    ModelAndView editOrder(@PathVariable(name = "id") Long id, OrderDTO orderDTO) {
        orderClient.updateOrder(id, orderDTO);
        return new ModelAndView(REDIRECTTXT);
    }
}