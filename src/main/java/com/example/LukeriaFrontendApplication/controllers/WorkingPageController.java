package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/WorkingPage")
public class WorkingPageController {
    private final PackageClient packageClient;
    private final ClientClient clientClient;
    private final OrderClient orderClient;
    private final ProductClient productClient;
    private final OrderProductClient orderProductClient;
    private static final String SESSION_TOKEN = "sessionToken";

    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<OrderProductDTO> orderProducts = orderProductClient.getAllOrderProducts(token);
        List<OrderDTO> orders = orderClient.getAllOrders(token);
        List<ClientDTO> clients = clientClient.getAllClients(token);
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        List<OrderDTO> ordersCopy = new ArrayList<>(orders);
        for (OrderDTO order : ordersCopy) {
            if (order.isInvoiced()) {
                orders.remove(order);
                orderProducts.removeIf(orderProduct -> orderProduct.getOrderId().equals(order.getId()));
            }
        }
        model.addAttribute("packages", packages);
        model.addAttribute("clients", clients);
        model.addAttribute("orders", orders);
        model.addAttribute("orderProducts", orderProducts);
        return "WorkingPage/show";
    }
}

