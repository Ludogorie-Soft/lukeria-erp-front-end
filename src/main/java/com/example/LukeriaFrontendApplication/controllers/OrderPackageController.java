package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.OrderClient;
import com.example.LukeriaFrontendApplication.config.OrderProductClient;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/orderProduct")
public class OrderPackageController {
    private final OrderProductClient orderProductClient;
    private final OrderClient orderClient;
    private final PackageClient packageClient;
    private static final String CARTONTXT = "orderProduct";
    private static final String REDIRECTTXT = "redirect:/orderProduct/show";

    @GetMapping("/addProduct")
    String createOrderProduct(Model model) {
        OrderProductDTO orderProduct = new OrderProductDTO();
        List<OrderDTO> orderDTOS = orderClient.getAllOrders();
        Long biggestId = null;
        for (OrderDTO orderDTO : orderDTOS) {
            Long currentId = orderDTO.getId();
            if (biggestId == null || (currentId != null && currentId > biggestId)) {
                biggestId = currentId;
            }
        }
        OrderDTO orderDTO = orderClient.getOrderById(biggestId);
        List<PackageDTO> packageDTOS = packageClient.getAllPackages();
        model.addAttribute("order", orderDTO);
        model.addAttribute("packages", packageDTOS);
        model.addAttribute(CARTONTXT, orderProduct);
        return "OrderProduct/addProduct";
    }

    @PostMapping("/submit")
    public ModelAndView submitOrderProduct(@ModelAttribute("orderProduct") OrderProductDTO orderProductDTO,
                                           @RequestParam(value = "addAnotherDish", required = false) boolean addAnotherDish, Model model) {
        List<OrderDTO> orderDTOS = orderClient.getAllOrders();
        Long biggestId = null;
        for (OrderDTO orderDTO : orderDTOS) {
            Long currentId = orderDTO.getId();
            if (biggestId == null || (currentId != null && currentId > biggestId)) {
                biggestId = currentId;
            }
        }

        orderProductDTO.setOrderId(biggestId);
        orderProductClient.createOrderProduct(orderProductDTO);
        if(addAnotherDish){
            OrderDTO orderDTO = orderClient.getOrderById(biggestId);
            model.addAttribute("order", orderDTO);
            List<PackageDTO> packageDTOS = packageClient.getAllPackages();
            model.addAttribute("packages", packageDTOS);
            return new ModelAndView("OrderProduct/addProduct");
        }
        return new ModelAndView(REDIRECTTXT);
    }
}
