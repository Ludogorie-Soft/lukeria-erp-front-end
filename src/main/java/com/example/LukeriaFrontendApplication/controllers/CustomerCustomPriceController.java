package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ClientClient;
import com.example.LukeriaFrontendApplication.config.CustomerCustomPriceClient;
import com.example.LukeriaFrontendApplication.config.ProductClient;
import com.example.LukeriaFrontendApplication.dtos.ClientDTO;
import com.example.LukeriaFrontendApplication.dtos.CustomerCustomPriceDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@RequestMapping("/customerCustomPrice")
public class CustomerCustomPriceController {

    private final CustomerCustomPriceClient customerCustomPriceClient;
    private final ClientClient clientClient;
    private final ProductClient productClient;

    @GetMapping("/create")
    public String createCustomerCustomPrice(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<ClientDTO> allClients = clientClient.getAllClients(token);
        List<ProductDTO> allProducts = productClient.getAllProducts(token);
        model.addAttribute("customPrice", new CustomerCustomPriceDTO());
        model.addAttribute("clients", allClients);
        model.addAttribute("products", allProducts);
        return "CustomPrice/create";
    }

    @PostMapping("/submit")
    public ModelAndView submitCustomerCustomPrice(@Valid @ModelAttribute("customPrice") CustomerCustomPriceDTO customerCustomPriceDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        customerCustomPriceClient.createCustomPriceForCustomer(customerCustomPriceDTO, token);
        return new ModelAndView("redirect:/client/show");
    }

}
