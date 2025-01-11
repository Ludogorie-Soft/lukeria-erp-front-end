package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.CartonDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.PlateDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    private final ImageClient imageService;
    private final ClientClient clientClient;
    private final CustomerCustomPriceClient customerCustomPriceClient;
    private final InvoiceClient invoiceClient;
    private final InvoiceOrderProductClient invoiceOrderProductClient;
    private final MaterialOrderClient materialOrderClient;
    private final MonthlyOrderClient monthlyOrderClient;
    private final MonthlyOrderProductClient monthlyOrderProductClient;
    private final OrderClient orderClient;
    private final OrderProductClient orderProductClient;
    private final QueryClient queryClient;
    private final ShoppingCartClient shoppingCartClient;
    private final UserClient userClient;

    @GetMapping("/show")
    public String showPackage(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        List<PlateDTO> plates = plateClient.getAllPlates(token);
        List<CartonDTO> cartons = cartonClient.getAllCartons(token);
        List<ProductDTO> productDTOS =productClient.getAllProducts(token);
        model.addAttribute("productDTOS", productDTOS);

        model.addAttribute("plates", plates);
        model.addAttribute("cartons", cartons);
        model.addAttribute("packages", packages);
        return "Database/show";
    }
}
