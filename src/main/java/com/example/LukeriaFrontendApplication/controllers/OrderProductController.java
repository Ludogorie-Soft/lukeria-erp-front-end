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
import java.util.Objects;
import java.util.stream.Collectors;


@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/orderProduct")
public class OrderProductController {
    private final OrderProductClient orderProductClient;
    private final OrderClient orderClient;
    private final PackageClient packageClient;
    private static final String CARTONTXT = "orderProduct";
    private static final String REDIRECTTXT = "redirect:/order/show";

    @GetMapping("/addProduct")
    String createOrderProduct(Model model) {
        OrderProductDTO orderProduct = new OrderProductDTO();
        OrderDTO orderDTO = orderClient.getOrderById(orderClient.findFirstByOrderByIdDesc().getId());
        List<OrderProductDTO> orderProductDTOS = orderProductClient.getAllOrderProducts().stream().filter(order -> Objects.equals(order.getOrderId(), orderDTO.getId())).toList();
        List<Long> packageDTOIds = orderProductDTOS.stream().map(OrderProductDTO::getPackageId).toList();
        List<PackageDTO> packageDTOList = packageDTOIds.stream().map(packageClient::getPackageById).collect(Collectors.toList());
        model.addAttribute("orderProducts", orderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("order", orderDTO);
        model.addAttribute("packages", packageClient.getAllPackages());
        model.addAttribute(CARTONTXT, orderProduct);
        return "OrderProduct/addProduct";
    }

    @PostMapping("/submit")
    public ModelAndView submitOrderProduct(@ModelAttribute("orderProduct") OrderProductDTO orderProductDTO,
                                           @RequestParam(value = "addAnotherDish", required = false) boolean addAnotherDish, Model model) {
        orderProductDTO.setOrderId(orderClient.findFirstByOrderByIdDesc().getId());
        orderProductClient.createOrderProduct(orderProductDTO);
        if (addAnotherDish) {
            OrderDTO orderDTO = orderClient.getOrderById(orderClient.findFirstByOrderByIdDesc().getId());
            model.addAttribute("order", orderDTO);
            model.addAttribute("packages", packageClient.getAllPackages());
            List<OrderProductDTO> orderProductDTOS = orderProductClient.getAllOrderProducts().stream().filter(order -> Objects.equals(order.getOrderId(), orderDTO.getId())).toList();
            List<Long> packageDTOIds = orderProductDTOS.stream().map(OrderProductDTO::getPackageId).toList();
            List<PackageDTO> packageDTOList = packageDTOIds.stream().map(packageClient::getPackageById).collect(Collectors.toList());
            model.addAttribute("orderProducts", orderProductDTOS);
            model.addAttribute("products", packageDTOList);
            return new ModelAndView("OrderProduct/addProduct");
        }
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/orderDetails/{orderId}")
    public String showOrderDetails(@PathVariable(name = "orderId") Long orderId, Model model) {
        List<OrderProductDTO> orderProductDTOS = orderProductClient.getAllOrderProducts().stream().filter(order -> Objects.equals(order.getOrderId(), orderId)).toList();
        List<Long> packageDTOIds = orderProductDTOS.stream().map(OrderProductDTO::getPackageId).toList();
        List<PackageDTO> packageDTOList = packageDTOIds.stream().map(packageClient::getPackageById).collect(Collectors.toList());
        model.addAttribute("order", orderClient.getOrderById(orderId));
        model.addAttribute("orderProducts", orderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("packages", packageClient.getAllPackages());
        return "OrderProduct/orderDetails";
    }

    @GetMapping("/addProductToExistingOrder/{orderId}")
    public String addProductToExistingOrder(@PathVariable("orderId") Long orderId, Model model) {
        OrderProductDTO orderProduct = new OrderProductDTO();
        OrderDTO orderDTO = orderClient.getOrderById(orderId);
        List<OrderProductDTO> orderProductDTOS = orderProductClient.getAllOrderProducts().stream().filter(order -> Objects.equals(order.getOrderId(), orderDTO.getId())).toList();
        List<Long> packageDTOIds = orderProductDTOS.stream().map(OrderProductDTO::getPackageId).toList();
        List<PackageDTO> packageDTOList = packageDTOIds.stream().map(packageClient::getPackageById).collect(Collectors.toList());
        model.addAttribute("orderProducts", orderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("order", orderDTO);
        model.addAttribute("packages", packageClient.getAllPackages());
        model.addAttribute(CARTONTXT, orderProduct);
        return "OrderProduct/addProductToExistingOrder";
    }
    @PostMapping("/submitExistingOrder")
    public ModelAndView submitExistingOrderProduct(@ModelAttribute("orderProduct") OrderProductDTO orderProductDTO, Model model,
                                                   @RequestParam("orderId") Long orderId) {
        orderProductDTO.setOrderId(orderId);
        orderProductClient.createOrderProduct(orderProductDTO);
        return new ModelAndView(REDIRECTTXT);
    }
    @PostMapping("/delete/{id}")
    ModelAndView deleteOrderProductById(@PathVariable("id") Long id, Model model) {
        orderProductClient.deleteOrderProductById(id);
        return new ModelAndView(REDIRECTTXT);
    }
}
