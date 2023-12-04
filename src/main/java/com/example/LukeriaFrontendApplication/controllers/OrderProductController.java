package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.OrderDTO;
import com.example.LukeriaFrontendApplication.dtos.OrderProductDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
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
    private final ProductClient productClient;
    private final PackageClient packageClient;
    private static final String CARTONTXT = "orderProduct";
    private static final String REDIRECTTXT = "redirect:/order/show";

    private final ImageClient imageService;

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    @GetMapping("/addProduct")
    String createOrderProduct(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        OrderProductDTO orderProduct = new OrderProductDTO();
        OrderDTO orderDTO = orderClient.getOrderById(orderClient.findFirstByOrderByIdDesc(token).getId(), token);
        List<OrderProductDTO> orderProductDTOS = orderProductClient.getAllOrderProducts(token).stream().filter(order -> Objects.equals(order.getOrderId(), orderDTO.getId())).toList();
        List<Long> packageDTOIds = orderProductDTOS.stream().map(OrderProductDTO::getPackageId).toList();
        List<PackageDTO> packageDTOList = packageDTOIds.stream()
                .map(id1 -> packageClient.getPackageById(id1, token))
                .collect(Collectors.toList());
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("orderProducts", orderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("order", orderDTO);
        List<PackageDTO> packageDTOS = new ArrayList<>();
        for (PackageDTO aPackage : packageClient.getAllPackages(token)) {
            for (ProductDTO productDTO : productClient.getAllProducts(token)) {
                if (Objects.equals(productDTO.getPackageId(), aPackage.getId()) && productDTO.getAvailableQuantity() > 0) {
                    packageDTOS.add(aPackage);
                }
            }
        }
        model.addAttribute("packages", packageDTOS);
        model.addAttribute(CARTONTXT, orderProduct);
        return "OrderProduct/addProduct";
    }

    @PostMapping("/submit")
    public ModelAndView submitOrderProduct(@ModelAttribute("orderProduct") OrderProductDTO orderProductDTO,
                                           @RequestParam(value = "addAnotherDish", required = false) boolean addAnotherDish, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        orderProductDTO.setOrderId(orderClient.findFirstByOrderByIdDesc(token).getId());
        orderProductClient.createOrderProduct(orderProductDTO, token);
        if (addAnotherDish) {
            OrderDTO orderDTO = orderClient.getOrderById(orderClient.findFirstByOrderByIdDesc(token).getId(), token);
            model.addAttribute("order", orderDTO);
            model.addAttribute("packages", packageClient.getAllPackages(token));
            List<OrderProductDTO> orderProductDTOS = orderProductClient.getAllOrderProducts(token).stream().filter(order -> Objects.equals(order.getOrderId(), orderDTO.getId())).toList();
            List<Long> packageDTOIds = orderProductDTOS.stream().map(OrderProductDTO::getPackageId).toList();
            List<PackageDTO> packageDTOList = packageDTOIds.stream().map
                            (id1 -> packageClient.getPackageById(id1, token))
                    .collect(Collectors.toList());
            model.addAttribute("orderProducts", orderProductDTOS);
            model.addAttribute("products", packageDTOList);
            model.addAttribute("backendBaseUrl", backendBaseUrl);
            return new ModelAndView("OrderProduct/addProduct");
        }
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/orderDetails/{orderId}")
    public String showOrderDetails(@PathVariable(name = "orderId") Long orderId, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<OrderProductDTO> orderProductDTOS = orderProductClient.getAllOrderProducts(token).stream().filter(order -> Objects.equals(order.getOrderId(), orderId)).toList();
        List<Long> packageDTOIds = orderProductDTOS.stream().map(OrderProductDTO::getPackageId).toList();
        List<PackageDTO> packageDTOList = packageDTOIds.stream()
                .map(id1 -> packageClient.getPackageById(id1, token))
                .collect(Collectors.toList());
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("order", orderClient.getOrderById(orderId, token));
        model.addAttribute("orderProducts", orderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("packages", packageClient.getAllPackages(token));
        return "OrderProduct/orderDetails";
    }

    @GetMapping("/addProductToExistingOrder/{orderId}")
    public String addProductToExistingOrder(@PathVariable("orderId") Long orderId, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        OrderProductDTO orderProduct = new OrderProductDTO();
        OrderDTO orderDTO = orderClient.getOrderById(orderId, token);
        List<OrderProductDTO> orderProductDTOS = orderProductClient.getAllOrderProducts(token).stream().filter(order -> Objects.equals(order.getOrderId(), orderDTO.getId())).toList();
        List<Long> packageDTOIds = orderProductDTOS.stream().map(OrderProductDTO::getPackageId).toList();
        List<PackageDTO> packageDTOList = packageDTOIds.stream()
                .map(id1 -> packageClient.getPackageById(id1, token))
                .collect(Collectors.toList());
        model.addAttribute("orderProducts", orderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("order", orderDTO);
        model.addAttribute("packages", packageClient.getAllPackages(token));
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute(CARTONTXT, orderProduct);
        return "OrderProduct/addProductToExistingOrder";
    }

    @PostMapping("/submitExistingOrder")
    public ModelAndView submitExistingOrderProduct(@ModelAttribute("orderProduct") OrderProductDTO orderProductDTO, Model model,
                                                   @RequestParam("orderId") Long orderId, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        orderProductDTO.setOrderId(orderId);
        orderProductClient.createOrderProduct(orderProductDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteOrderProductById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        orderProductClient.deleteOrderProductById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }
}
