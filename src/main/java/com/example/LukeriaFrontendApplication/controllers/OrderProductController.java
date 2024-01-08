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
import java.util.List;
import java.util.Objects;


@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/orderProduct")
public class OrderProductController {
    private static final String ORDERPRODUCT = "orderProduct";
    private static final String REDIRECTTXT = "redirect:/order/show";
    private final OrderProductClient orderProductClient;
    private final OrderClient orderClient;
    private final ProductClient productClient;
    private final ClientClient clientClient;
    private final MonthlyOrderClient monthlyOrderClient;
    private final MonthlyOrderProductClient monthlyOrderProductClient;
    private final PackageClient packageClient;
    private final ImageClient imageService;

    @Value("${backend.base-url}/images")
    private String backendBaseUrl;

    @GetMapping("/addProduct")
    String createOrderProduct(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        OrderDTO orderDTO = orderClient.getOrderById(orderClient.findFirstByOrderByIdDesc(token).getId(), token);
        List<OrderProductDTO> orderProductDTOS = getOrderProductsForOrder(orderDTO, token);
        List<PackageDTO> packageDTOList = getPackageDTOListForOrderProducts(orderProductDTOS, token);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("orderProducts", orderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("order", orderDTO);
        List<PackageDTO> packageDTOS;
        if (OrderController.isMonthlyOrder) {
            packageDTOS = getAvailablePackagesForOrder(orderDTO, token);
        } else {
            packageDTOS = getAvailablePackagesFromAllPackages(token);
        }
        model.addAttribute("packages", packageDTOS);

        OrderProductDTO orderProduct = new OrderProductDTO();
        model.addAttribute(ORDERPRODUCT, orderProduct);

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
            List<OrderProductDTO> orderProductDTOS = getOrderProductsForOrder(orderDTO, token);
            List<PackageDTO> packageDTOList = getPackageDTOListForOrderProducts(orderProductDTOS, token);
            List<PackageDTO> packageDTOS;
            if (OrderController.isMonthlyOrder) {
                packageDTOS = getAvailablePackagesForOrder(orderDTO, token);
            } else {
                packageDTOS = getAvailablePackagesFromAllPackages(token);
            }
            model.addAttribute("packages", packageDTOS);
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
                .map(id1 -> packageClient.getPackageById(id1, token)).toList();
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
        List<OrderProductDTO> orderProductDTOS = getOrderProductsForOrder(orderDTO, token);
        List<PackageDTO> packageDTOList = getPackageDTOListForOrderProducts(orderProductDTOS, token);
        List<PackageDTO> packageDTOS = getAvailablePackagesFromAllPackages(token);
        model.addAttribute("orderProducts", orderProductDTOS);
        model.addAttribute("products", packageDTOList);
        model.addAttribute("order", orderDTO);
        model.addAttribute("packages", packageDTOS);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute(ORDERPRODUCT, orderProduct);
        return "OrderProduct/addProductToExistingOrder";
    }

    private List<OrderProductDTO> getOrderProductsForOrder(OrderDTO orderDTO, String token) {
        return orderProductClient.getAllOrderProducts(token)
                .stream()
                .filter(order -> Objects.equals(order.getOrderId(), orderDTO.getId()))
                .toList();
    }

    private List<PackageDTO> getPackageDTOListForOrderProducts(List<OrderProductDTO> orderProductDTOS, String token) {
        List<Long> packageDTOIds = orderProductDTOS.stream().map(OrderProductDTO::getPackageId).toList();
        return packageDTOIds.stream()
                .map(id1 -> packageClient.getPackageById(id1, token))
                .toList();
    }

    private List<PackageDTO> getAvailablePackagesForOrder(OrderDTO orderDTO, String token) {
        List<PackageDTO> packageDTOS = new ArrayList<>();

        for (MonthlyOrderDTO monthlyOrder : monthlyOrderClient.getAllMonthlyOrders(token)) {
            if (Objects.equals(orderDTO.getClientId(), monthlyOrder.getClientId()) && !orderDTO.isInvoiced()) {
                packageDTOS.addAll(getAvailablePackagesFromMonthlyOrder(monthlyOrder, token));
            }
        }

        if (packageDTOS.isEmpty()) {
            packageDTOS.addAll(getAvailablePackagesFromAllPackages(token));
        }

        return packageDTOS;
    }

    private List<PackageDTO> getAvailablePackagesFromMonthlyOrder(MonthlyOrderDTO monthlyOrder, String token) {
        List<PackageDTO> packageDTOS = new ArrayList<>();
        for (MonthlyOrderProductDTO monthlyOrderProductDTO : monthlyOrderProductClient.getAllMonthlyProductOrders(token)) {
            for (ProductDTO productDTO : productClient.getAllProducts(token)) {
                if (Objects.equals(productDTO.getPackageId(), monthlyOrderProductDTO.getPackageId()) && productDTO.getAvailableQuantity() > 0) {
                    PackageDTO packageDTO = packageClient.getPackageById(monthlyOrderProductDTO.getPackageId(), token);
                    if (!packageDTOS.contains(packageDTO)) {
                        packageDTOS.add(packageDTO);
                    }
                }
            }
        }
        return packageDTOS;
    }

    private List<PackageDTO> getAvailablePackagesFromAllPackages(String token) {
        List<PackageDTO> packageDTOS = new ArrayList<>();
        for (PackageDTO aPackage : packageClient.getAllPackages(token)) {
            for (ProductDTO productDTO : productClient.getAllProducts(token)) {
                if (Objects.equals(productDTO.getPackageId(), aPackage.getId()) && productDTO.getAvailableQuantity() > 0) {
                    packageDTOS.add(aPackage);
                }
            }
        }
        return packageDTOS;
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
