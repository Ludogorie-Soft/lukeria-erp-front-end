package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import com.example.LukeriaFrontendApplication.models.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/database")
public class DatabaseController {
    private static final String REDIRECTTXT = "redirect:/database/show";
    private static final String SESSION_TOKEN = "sessionToken";
    private final PackageClient packageClient;
    private final CartonClient cartonClient;
    private final ProductClient productClient;
    private final PlateClient plateClient;
    private final ClientClient clientClient;
    private final OrderClient orderClient;
    private final OrderProductClient orderProductClient;
    private final CustomerCustomPriceClient customerCustomPriceClient;
    private final InvoiceClient invoiceClient;
    private final InvoiceOrderProductClient invoiceOrderProductClient;
    private final MaterialOrderClient materialOrderClient;
    private final MonthlyOrderClient monthlyOrderClient;
    private final MonthlyOrderProductClient monthlyOrderProductClient;
    private final UserClient userClient;

    @GetMapping("/show")
    public String showPackage(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        List<PlateDTO> plates = plateClient.getAllPlates(token);
        List<CartonDTO> cartons = cartonClient.getAllCartons(token);
        List<ProductDTO> productDTOS = productClient.getAllProducts(token);
        List<ClientDTO> clientDTOS = clientClient.getAllClients(token);
        List<OrderDTO> orderDTOS = orderClient.getAllOrders(token);
        List<OrderProductDTO> orderProductDTOS = orderProductClient.getAllOrderProducts(token);
        List<InvoiceDTO> invoiceDTOS = invoiceClient.getAllInvoices(token);
        List<CustomerCustomPriceDTO> customerCustomPriceDTOS = customerCustomPriceClient.getAllCustomPrices(token);
        List<InvoiceOrderProductDTO> invoiceOrderProductDTOS = invoiceOrderProductClient.getAllInvoiceOrderProduct(token);
        List<MaterialOrderDTO> materialOrderDTOS = materialOrderClient.getAllMaterialOrders(token);
        List<MonthlyOrderDTO> monthlyOrderDTOS = monthlyOrderClient.getAllMonthlyOrders(token);
        List<MonthlyOrderProductDTO> monthlyOrderProductDTOS = monthlyOrderProductClient.getAllMonthlyProductOrders(token);
        List<UserDTO> userDTOS = userClient.getAllUsers(token);
        model.addAttribute("monthlyOrderProductDTOS", monthlyOrderProductDTOS);
        model.addAttribute("monthlyOrderDTOS", monthlyOrderDTOS);
        model.addAttribute("userDTOS", userDTOS);
        model.addAttribute("materialOrderDTOS", materialOrderDTOS);
        model.addAttribute("invoiceOrderProductDTOS", invoiceOrderProductDTOS);
        model.addAttribute("customerCustomPriceDTOS", customerCustomPriceDTOS);
        model.addAttribute("invoiceDTOS", invoiceDTOS);
        model.addAttribute("orderDTOS", orderDTOS);
        model.addAttribute("orderProductDTOS", orderProductDTOS);
        model.addAttribute("clientDTOS", clientDTOS);
        model.addAttribute("productDTOS", productDTOS);
        model.addAttribute("plates", plates);
        model.addAttribute("cartons", cartons);
        model.addAttribute("packages", packages);
        return "Database/show";
    }

    @PostMapping("/edit/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute OrderProductDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        orderProductClient.updateOrderProduct(id, object, token);

        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/product/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute ProductDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        productClient.updateProduct(id, object, token);

        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/plate/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute PlateDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        plateClient.updatePlate(id, object, token);

        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/carton/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute CartonDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        cartonClient.updateCarton(id, object, token);

        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/package/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute PackageDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        packageClient.updatePackage(id, object, token);

        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/user/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute UserDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        userClient.updateUser(id, object, token);

        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/client/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute ClientDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        clientClient.updateClient(id, object, token);

        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/customerCustomPrice")
    public ModelAndView editObjects(@ModelAttribute CustomerCustomPriceDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        customerCustomPriceClient.updateCustomPrice(object, token);

        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/invoice/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute InvoiceDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        invoiceClient.updateInvoice(id,object, token);

        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/order/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute OrderDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        orderClient.updateOrder(id,object, token);

        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/invoiceOrderProduct/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute InvoiceOrderProductDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        invoiceOrderProductClient.updateInvoiceOrderProduct(id,object, token);

        return new ModelAndView(REDIRECTTXT);
    }
    @PostMapping("/edit/materialOrder/{id}")
    public ModelAndView editObjects(@PathVariable(name = "id") Long id,
                                    @ModelAttribute MaterialOrderDTO object,
                                    HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        materialOrderClient.updateMaterialOrder(id,object, token);

        return new ModelAndView(REDIRECTTXT);
    }
}
