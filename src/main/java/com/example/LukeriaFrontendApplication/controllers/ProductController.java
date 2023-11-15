package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ImageClient;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.config.ProductClient;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product")
public class ProductController {
    private final ProductClient productClient;
    private final PackageClient packageClient;
    private static final String REDIRECTTXT = "redirect:/product/show";
    private final ImageClient imageService;
    @Value("${backend.base-url}")
    private String backendBaseUrl;


    @GetMapping("/show")
    public String showAllProducts(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<ProductDTO> products = productClient.getAllProducts(token);
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
        model.addAttribute("productPackageMapImages", productPackageMapImages);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("products", products);
        model.addAttribute("packages", packages);
        model.addAttribute("productPackageMap", productPackageMap);
        return "Product/show";
    }

    @GetMapping("/create")
    public String createProduct(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        ProductDTO product = new ProductDTO();
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("packages", packages);
        model.addAttribute("product", product);
        return "Product/create";
    }

    @PostMapping("/submit")
    public ModelAndView submitProduct(@ModelAttribute("product") ProductDTO productDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        productClient.createProduct(productDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteProductById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        productClient.deleteProductById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/editProduct/{id}")
    String editProduct(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        ProductDTO existingProduct = productClient.getProductById(id, token);
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        model.addAttribute("packages", packages);
        model.addAttribute("product", existingProduct);
        return "Product/edit";
    }

    @PostMapping("/editSubmit/{id}")
    ModelAndView editPackage(@PathVariable(name = "id") Long id, ProductDTO productDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        productClient.updateProduct(id, productDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/produce")
    public String produceProduct(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<ProductDTO> products = productClient.getAllProducts(token);
        List<PackageDTO> packages = packageClient.getAllPackages(token);

        Map<Long, String> productPackageMap = new HashMap<>();
        for (PackageDTO packageDTO : packages) {
            productPackageMap.put(packageDTO.getId(), packageDTO.getName());
        }
        Map<Long, String> productPackageMapImages = new HashMap<>();
        for (PackageDTO packageDTO : packages) {
            productPackageMapImages.put(packageDTO.getId(), packageDTO.getPhoto());
        }
        model.addAttribute("products", products);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("productPackageMap", productPackageMap);
        model.addAttribute("productPackageMapImages", productPackageMapImages);
        return "Product/produce";
    }

    @PostMapping("/produce")
    ModelAndView produceProduct(@RequestParam("productId") Long productId, @RequestParam("producedQuantity") int producedQuantity, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        productClient.produceProduct(productId, producedQuantity, token);
        return new ModelAndView(REDIRECTTXT);
    }
}
