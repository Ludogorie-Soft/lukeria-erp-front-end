package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ClientClient;
import com.example.LukeriaFrontendApplication.config.OrderClient;
import com.example.LukeriaFrontendApplication.config.OrderProductClient;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.dtos.ClientDTO;
import com.example.LukeriaFrontendApplication.dtos.OrderDTO;
import com.example.LukeriaFrontendApplication.dtos.OrderProductDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order")
public class OrderController {
    private final OrderClient orderClient;
    private final ClientClient clientClient;
    private static final String CARTONTXT = "order";
    private static final String REDIRECTTXT = "redirect:/orderProduct/show";

    @GetMapping("/create")
    String createOrder(Model model) {
        OrderDTO orderDTO = new OrderDTO();
        List<ClientDTO> clientDTOS = clientClient.getAllClients();
        model.addAttribute("clients", clientDTOS);
        model.addAttribute(CARTONTXT, orderDTO);
        return "OrderProduct/create";
    }

    @PostMapping("/submit")
    public ModelAndView submitOrder(@ModelAttribute("order") OrderDTO orderDTO) {
        orderClient.createOrder(orderDTO);
        return new ModelAndView("redirect:/orderProduct/addProduct");
    }
}