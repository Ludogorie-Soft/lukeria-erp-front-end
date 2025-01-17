package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ClientClient;
import com.example.LukeriaFrontendApplication.config.CustomerCustomPriceClient;
import com.example.LukeriaFrontendApplication.config.ImageClient;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.config.ProductClient;
import com.example.LukeriaFrontendApplication.dtos.ClientDTO;
import com.example.LukeriaFrontendApplication.dtos.CustomerCustomPriceDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductPriceDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/customerCustomPrice")
public class CustomerCustomPriceController {

    private final CustomerCustomPriceClient customerCustomPriceClient;
    private final ClientClient clientClient;
    private final ProductClient productClient;
    private final PackageClient packageClient;
    private final ImageClient imageService;
    private static final String S3bucketImagesLink = "https://lukeria-images.s3.eu-central-1.amazonaws.com";

    @Value("${backend.base-url}/images")
    private String backendBaseUrl;


    @GetMapping("/create")
    public String createCustomerCustomPrice(@RequestParam(name = "clientId") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<CustomerCustomPriceDTO> allCustomPrice = customerCustomPriceClient.getAllCustomPrices(token);
        List<ProductDTO> allProducts = productClient.getAllProducts(token);
        ClientDTO client = clientClient.getClientById(id, token);

        // Филтрираме продуктите, за да премахнем тези, които вече имат персонализирана цена за дадения клиент
        List<ProductDTO> filteredProducts = allProducts.stream()
                .filter(product -> allCustomPrice.stream()
                        .noneMatch(customPrice -> customPrice.getProductId().equals(product.getId()) && customPrice.getClientId().equals(id)))
                .collect(Collectors.toList());

        List<PackageDTO> packages = packageClient.getAllPackages(token);

        Map<Long, String> productPackageMap = new HashMap<>();
        for (PackageDTO packageDTO : packages) {
            productPackageMap.put(packageDTO.getId(), packageDTO.getName());
        }

        model.addAttribute("customPrice", new CustomerCustomPriceDTO());
        model.addAttribute("client", client);
        model.addAttribute("products", filteredProducts);
        model.addAttribute("productPackageMap", productPackageMap);

        return "CustomPrice/create";
    }

    @PostMapping("/submit")
    public ModelAndView submitCustomerCustomPrice(@Valid @ModelAttribute("customPrice") CustomerCustomPriceDTO customerCustomPriceDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        customerCustomPriceClient.createCustomPriceForCustomer(customerCustomPriceDTO, token);
        return new ModelAndView("redirect:/customerCustomPrice/allForClient/" + customerCustomPriceDTO.getClientId());
    }

    @GetMapping("/allForClient/{id}")
    public String allProductsForClient(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<CustomerCustomPriceDTO> allCustomPricesForClient = customerCustomPriceClient.allProductsWithAndWithoutCustomPrice(id, token);
        List<ProductPriceDTO> allProductsWithPrice = new ArrayList<>();

        for (CustomerCustomPriceDTO customPriceDTO : allCustomPricesForClient) {
            ProductDTO productDTO = productClient.getProductById(customPriceDTO.getProductId(), token);
            allProductsWithPrice.add(new ProductPriceDTO(productDTO, customPriceDTO.getPrice()));
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
        model.addAttribute("allProducts", allProductsWithPrice);
        model.addAttribute("productPackageMapImages", productPackageMapImages);
        model.addAttribute("backendBaseUrl", S3bucketImagesLink);
        model.addAttribute("packages", packages);
        model.addAttribute("productPackageMap", productPackageMap);
        model.addAttribute("clientId", id);
        return "CustomPrice/show";
    }

    @PostMapping("/delete")
    public ModelAndView delete(@RequestParam(name = "clientId") Long clientId, @RequestParam(name = "productId") Long productId, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        customerCustomPriceClient.deleteCustomPrice(clientId, productId,token);
        return new ModelAndView("redirect:/customerCustomPrice/allForClient/" + clientId);
    }
    @GetMapping("/edit")
    public String update(@RequestParam(name = "clientId") Long clientId, @RequestParam(name = "productId") Long productId,@RequestParam(name = "price")BigDecimal price, HttpServletRequest request,Model model) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        ClientDTO client = clientClient.getClientById(clientId, token);
        ProductDTO product = productClient.getProductById(productId, token);

        CustomerCustomPriceDTO customPriceDTO = new CustomerCustomPriceDTO();
        customPriceDTO.setPrice(price);

        model.addAttribute("customPrice", customPriceDTO);
        model.addAttribute("client", client);
        model.addAttribute("product", product);

        return "CustomPrice/edit";
    }
    @PostMapping("/edit")
    public ModelAndView submitEdit(@Valid @ModelAttribute("customPrice") CustomerCustomPriceDTO customerCustomPriceDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        customerCustomPriceClient.updateCustomPrice(customerCustomPriceDTO,token);
        return new ModelAndView("redirect:/customerCustomPrice/allForClient/" + customerCustomPriceDTO.getClientId());
    }
}
