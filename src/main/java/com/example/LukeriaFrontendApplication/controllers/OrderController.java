package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ImageClient imageService;

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

    @PostMapping("/admin/submit")
    public ModelAndView submitOrder(@ModelAttribute("order") OrderDTO orderDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        orderClient.createOrder(orderDTO, token);
        return new ModelAndView("redirect:/orderProduct/addProduct");
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam("product.id") Long productId, @RequestParam("quantity") int quantity, HttpServletRequest request) {
        OrderDTO orderDTO = new OrderDTO();
        String token = (String) request.getSession().getAttribute("sessionToken");
        Long clientId = 0L;

        UserDTO userDTO = userClient.findAuthenticatedUser(token);
        List<ClientUserDTO> clientUserDTOS = clientUserClient.getAllClientUsers(token);
        for (ClientUserDTO clientUserDTO : clientUserDTOS) {
            if (clientUserDTO.getUserId().equals(userDTO.getId())) {
                orderDTO.setClientId(clientUserDTO.getClientId());
                clientId = clientUserDTO.getClientId();
            }
        }

        orderDTO.setOrderDate(java.sql.Date.valueOf(LocalDate.now()));
        orderDTO.setInvoiced(false);

        ProductDTO productDTO = productClient.getProductById(productId, token);
        Long orderId = orderClient.createOrder(orderDTO, token).getBody().getId();
        if ((orderId != null)) {
            OrderProductDTO orderProductDTO = new OrderProductDTO();
            orderProductDTO.setOrderId(orderId);
            orderProductDTO.setNumber(quantity);
            orderProductDTO.setPackageId(productDTO.getPackageId());
            try {
                orderProductDTO.setSellingPrice(customerCustomPriceClient.customPriceByClientAndProduct(clientId, productId, token).getPrice());
            } catch (FeignException.NotFound e) {
                orderProductDTO.setSellingPrice(productDTO.getPrice());
            }
            orderProductClient.createOrderProduct(orderProductDTO, token);
            return "Order/buy";
        }
        return "redirect:/order/place";
    }


    @GetMapping("/place")
    public String chooseQuantityAndPayment(@RequestParam("product.id") Long productId, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        ProductDTO productDTO = productClient.getProductById(productId, token);
        UserDTO userDTO = userClient.findAuthenticatedUser(token);
        List<ClientUserDTO> clientUserDTOS = clientUserClient.getAllClientUsers(token);
        for (int i = 0; i < clientUserDTOS.size(); i++) {
            if (clientUserDTOS.get(i).getUserId().equals(userDTO.getId())) {
                try {
                    Optional<CustomerCustomPriceDTO> customerCustomPriceDTO =
                            Optional.ofNullable(customerCustomPriceClient.customPriceByClientAndProduct(
                                    clientUserDTOS.get(i).getClientId(), productId, token));

                    customerCustomPriceDTO.ifPresent(customPriceDTO ->
                            model.addAttribute("price", customPriceDTO.getPrice()));
                } catch (FeignException.NotFound ignored) {
                }
            }
        }
        PackageDTO packageDTO = packageClient.getPackageById(productDTO.getPackageId(), token);

        String productImageUrl = (packageDTO.getPhoto() != null)
                ? backendBaseUrl + "/" + packageDTO.getPhoto()
                : "/img/photos/noImage.png";

        model.addAttribute("product", productDTO);
        model.addAttribute("productImageUrl", productImageUrl);
        return "Order/chooseQuantity";
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

        List<OrderDTO> orderDTOS = new ArrayList<>();

        for (ClientUserDTO clientUserDTO : clientUserDTOS) {
            if (clientUserDTO.getUserId().equals(userDTO.getId())) {
                List<OrderWithProductsDTO> orderWithProductsDTOS =
                        orderProductClient.getOrderProductDTOsByOrderDTOs(clientUserDTO.getClientId(), token).getBody();

                if (orderWithProductsDTOS != null) {
                    for (OrderWithProductsDTO orderWithProducts : orderWithProductsDTOS) {
                        orderDTOS.add(orderWithProducts.getOrderDTO());
                    }
                }
                model.addAttribute("orderList", orderDTOS);
                return "Order/myOrders";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/orderedProducts/{id}")
    public String orderProductsFromOrder(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        UserDTO userDTO = userClient.findAuthenticatedUser(token);
        List<ClientUserDTO> clientUserDTOS = clientUserClient.getAllClientUsers(token);
        List<OrderProductDTO> orderedProductsDTOs = new ArrayList<>();
        List<OrderProductHelper> orderProductsForShowing = new ArrayList<>();
        List<ProductDTO> allProducts = productClient.getAllProducts(token);

        boolean flag = false;

        for(ClientUserDTO clientUserDTO : clientUserDTOS){
            if(clientUserDTO.getUserId().equals(userDTO.getId())){
                flag = true;
            }
        }

        if (flag) {
            orderedProductsDTOs = orderProductClient.orderProducts(id, token);
        }

        for(OrderProductDTO orderProductDTO : orderedProductsDTOs){
            OrderProductHelper orderProductHelper = new OrderProductHelper();
            ProductDTO productDTO = allProducts.stream()
                    .filter(product -> product.getPackageId().equals(orderProductDTO.getPackageId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Product not found for package ID: " + orderProductDTO.getPackageId()));
            orderProductHelper.setOrderId(orderProductDTO.getOrderId());
            orderProductHelper.setProduct(productDTO);
            orderProductHelper.setQuantity(orderProductDTO.getNumber());
            orderProductHelper.setPrice(orderProductDTO.getSellingPrice().multiply(BigDecimal.valueOf(orderProductDTO.getNumber())));
            orderProductsForShowing.add(orderProductHelper);
        }

        List<PackageDTO> packages = packageClient.getAllPackages(token);

        Map<Long, String> productPackageMap = new HashMap<>();
        for (PackageDTO packageDTO : packages) {
            productPackageMap.put(packageDTO.getId(), packageDTO.getName());
        }
        Map<Long, String> productPackageMapImages = new HashMap<>();
        for (PackageDTO packageDTO : packages) {
            if (packageDTO.getPhoto() != null) {
                productPackageMapImages.put(packageDTO.getId(), packageDTO.getPhoto());
            }
        }
        for (PackageDTO packageDTO : packages) {
            if (packageDTO.getPhoto() != null) {
                imageService.getImage(packageDTO.getPhoto());
            }
        }
        model.addAttribute("items", orderProductsForShowing);
        model.addAttribute("productPackageMapImages", productPackageMapImages);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("packages", packages);
        model.addAttribute("productPackageMap", productPackageMap);

        return "Order/orderProducts";
    }


    @GetMapping("/buyPage")
    private String buyPage() {
        return "Order/buy";
    }

    @PostMapping("/createFromCart")
    public String createOrderFormShoppingCart(HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        orderClient.createOrder(token);
        return "redirect:/order/buyPage";
    }

}