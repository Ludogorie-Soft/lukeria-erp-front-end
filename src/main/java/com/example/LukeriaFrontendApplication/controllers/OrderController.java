package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order")
public class OrderController {
    public static boolean isMonthlyOrder;
    private static final String ORDERTXT = "order";
    private static final String REDIRECTTXT = "redirect:/order/show";
    private final OrderClient orderClient;
    private final UserClient userClient;
    private final ClientClient clientClient;
    private final ClientUserClient clientUserClient;
    private final MonthlyOrderClient monthlyOrderClient;
    private final ProductClient productClient;
    private final PackageClient packageClient;
    private final OrderProductClient orderProductClient;
    private final CustomerCustomPriceClient customerCustomPriceClient;
    @Value("${backend.base-url}/images")
    private String backendBaseUrl;

    @GetMapping("/create")
    String createOrder(Model model, HttpServletRequest request) {
        isMonthlyOrder = false;
        String token = (String) request.getSession().getAttribute("sessionToken");
        OrderDTO orderDTO = new OrderDTO();
        List<ClientDTO> clientDTOS = clientClient.getAllClients(token);
        model.addAttribute("clients", clientDTOS);
        model.addAttribute(ORDERTXT, orderDTO);
        return "OrderProduct/create";
    }

    @GetMapping("/monthly/create")
    String createOrderMonthly(Model model, HttpServletRequest request) {
        isMonthlyOrder = true;
        String token = (String) request.getSession().getAttribute("sessionToken");
        OrderDTO orderDTO = new OrderDTO();
        List<ClientDTO> clientDTOS = new ArrayList<>();
        for (ClientDTO clientDTO : clientClient.getAllClients(token)) {
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

    //    @PostMapping("/submit")
//    public ModelAndView submitOrder(@ModelAttribute("order") OrderDTO orderDTO, HttpServletRequest request) {
//        String token = (String) request.getSession().getAttribute("sessionToken");
//        orderClient.createOrder(orderDTO, token);
//        return new ModelAndView("redirect:/orderProduct/addProduct");
//    }
    @PostMapping("/submit")
    public String submitOrder(@RequestParam("product.id") Long productId, @RequestParam("quantity") int quantity, HttpServletRequest request) {
        OrderDTO orderDTO = new OrderDTO();
        String token = (String) request.getSession().getAttribute("sessionToken");
        Long clientId = 0L;

        // Get clientId from the logged-in user with the Customer role
        UserDTO userDTO = userClient.findAuthenticatedUser(token);
        List<ClientUserDTO> clientUserDTOS = clientUserClient.getAllClientUsers(token);
        for (ClientUserDTO clientUserDTO : clientUserDTOS) {
            if (clientUserDTO.getUserId().equals(userDTO.getId())) {
                orderDTO.setClientId(clientUserDTO.getClientId());
                clientId = clientUserDTO.getClientId();
            }
        }

        // Set the orderDate to now
        orderDTO.setOrderDate(java.sql.Date.valueOf(LocalDate.now()));

        // Set invoiced to false
        orderDTO.setInvoiced(false);

        // Submit the order
        ProductDTO productDTO = productClient.getProductById(productId, token);
        Long orderId = orderClient.createOrder(orderDTO, token).getBody().getId();
        if ((orderId != null)) {
            OrderProductDTO orderProductDTO = new OrderProductDTO();
            orderProductDTO.setOrderId(orderId);
            orderProductDTO.setNumber(quantity);
            orderProductDTO.setPackageId(productDTO.getPackageId());
            orderProductDTO.setSellingPrice(customerCustomPriceClient.customPriceByClientAndProduct(clientId, productId, token).getPrice().multiply(BigDecimal.valueOf(quantity)));
            orderProductClient.createOrderProduct(orderProductDTO, token);
            return "Order/buy";
        }
//
        // If there is an error, stay on the order creation page
        return "redirect:/order/place";
    }

    @GetMapping("/place")
    public String chooseQuantityAndPayment(@RequestParam("product.id") Long productId, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        // Retrieve the product by productId
        ProductDTO productDTO = productClient.getProductById(productId, token);
        UserDTO userDTO = userClient.findAuthenticatedUser(token);
        List<ClientUserDTO> clientUserDTOS = clientUserClient.getAllClientUsers(token);
        for (int i = 0; i < clientUserDTOS.size(); i++) {
            if (clientUserDTOS.get(i).getUserId().equals(userDTO.getId())) {
                Optional<CustomerCustomPriceDTO> customerCustomPriceDTO = Optional.ofNullable(customerCustomPriceClient.customPriceByClientAndProduct(clientUserDTOS.get(i).getClientId(), productId, token));
                customerCustomPriceDTO.ifPresent(customPriceDTO -> model.addAttribute("price", customPriceDTO.getPrice()));
            }
        }
        // Get package details if necessary
        PackageDTO packageDTO = packageClient.getPackageById(productDTO.getPackageId(), token);

        // Prepare the image URL
        String productImageUrl = (packageDTO.getPhoto() != null)
                ? backendBaseUrl + "/" + packageDTO.getPhoto()
                : "/img/photos/noImage.png";

        // Add the product and image to the model
        model.addAttribute("product", productDTO);
        model.addAttribute("productImageUrl", productImageUrl);
        return "Order/chooseQuantity"; // Return the view for choosing quantity and payment
    }


    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<OrderDTO> orders = orderClient.getAllOrders(token);
        List<Long> clientIds = orders.stream().map(OrderDTO::getClientId).toList();
        List<ClientDTO> clients = new java.util.ArrayList<>(clientIds.stream()
                .map(id1 -> clientClient.getClientById(id1, token)).toList());
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


    @GetMapping("/my-orders")
    String getOrdersForClient(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        UserDTO userDTO = userClient.findAuthenticatedUser(token);
        List<ClientUserDTO> clientUserDTOS = clientUserClient.getAllClientUsers(token);

        // Map to store package names and corresponding product links
        Map<Long, String> packageNames = new HashMap<>();
        Map<Long, String> productLinks = new HashMap<>();

        for (ClientUserDTO clientUserDTO : clientUserDTOS) {
            if (clientUserDTO.getUserId().equals(userDTO.getId())) {
                // Fetch the orders and products for the client
                List<OrderWithProductsDTO> orderWithProductsDTOS =
                        orderProductClient.getOrderProductDTOsByOrderDTOs(clientUserDTO.getClientId(), token).getBody();

                if (orderWithProductsDTOS != null) {
                    for (OrderWithProductsDTO orderWithProducts : orderWithProductsDTOS) {
                        for (OrderProductDTO orderProductDTO : orderWithProducts.getOrderProductDTOs()) {
                            // Fetch the package name and store it in the map
                            String packageName = packageClient.getPackageById(orderProductDTO.getPackageId(), token).getName();
                            packageNames.put(orderProductDTO.getId(), packageName);

                            // Fetch the product by package ID
                            ProductDTO productDTO = productClient.getProductByPackage(orderProductDTO.getPackageId(), token).getBody();
                            if (productDTO != null) {
                                // Generate the link for the product and store it
                                String productLink = "http://localhost:8080/order/place?product.id=" + productDTO.getId() + "&quantity=1";
                                productLinks.put(orderProductDTO.getId(), productLink);
                            }
                        }
                    }
                }

                // Add the fetched data to the Thymeleaf model
                model.addAttribute("orderWithProductsList", orderWithProductsDTOS);
                model.addAttribute("packageNames", packageNames);
                model.addAttribute("productLinks", productLinks);

                return "Order/myOrders";
            }
        }
        return "redirect:/login";
    }




//        String token = (String) request.getSession().getAttribute("sessionToken");
//        UserDTO userDTO = userClient.findAuthenticatedUser(token);
//        List<ClientUserDTO> clientUserDTOS = clientUserClient.getAllClientUsers(token);
//        for (int i = 0; i < clientUserDTOS.size(); i++) {
//            if (clientUserDTOS.get(i).getUserId().equals(userDTO.getId())) {
//                List<OrderDTO> orderDTOS = orderClient.getOrdersForClient(clientUserDTOS.get(i).getClientId(), token).getBody();
//                List<OrderProductDTO> orderProductDTOS = orderProductClient.
//                model.addAttribute("orderDTOs", orderDTOS);
//                return "Order/myOrders";
//            }
//        }
//        return "redirect:/login";
}