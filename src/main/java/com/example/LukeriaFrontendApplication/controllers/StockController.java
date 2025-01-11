package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.CartonDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.PlateDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/stock")
public class StockController {
    private static final String SESSION_TOKEN = "sessionToken";
    private final ProductClient productClient;
    private final PackageClient packageClient;
    private final PlateClient plateClient;
    private final CartonClient cartonClient;
    private final ImageClient imageService;

    @Value("${backend.base-url}/images")
    private String backendBaseUrl;

    @GetMapping("/show")
    public String showAllStocks(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);

        // Fetch data from clients
        List<ProductDTO> sortedProducts = productClient.getAllProducts(token);
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        List<PlateDTO> plates = plateClient.getAllPlates(token);
        List<CartonDTO> cartons = cartonClient.getAllCartons(token);

        Map<Long, ProductDTO> productMap = mapById(sortedProducts);
        Map<Long, CartonDTO> cartonMap = mapById(cartons);
        Map<Long, PlateDTO> plateMap = mapById(plates);

        Map<Long, String> productPackageMap = mapPackageNamesById(packages);
        Map<Long, String> productPackageMapImages = mapPackageImagesById(packages);

        model.addAttribute("plateMap", plateMap);
        model.addAttribute("cartonMap", cartonMap);
        model.addAttribute("productMap", productMap);
        model.addAttribute("packageImages", productPackageMapImages);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("products", sortedProducts);
        model.addAttribute("packages", packages);
        model.addAttribute("cartons", cartons);
        model.addAttribute("plates", plates);
        model.addAttribute("productPackageMap", productPackageMap);

        return "Stock/show";
    }

    private <T> Map<Long, T> mapById(List<T> items) {
        return items.stream()
                .collect(Collectors.toMap(this::getId, item -> item));
    }

    private Long getId(Object item) {
        if (item instanceof ProductDTO) {
            return ((ProductDTO) item).getId();
        } else if (item instanceof CartonDTO) {
            return ((CartonDTO) item).getId();
        } else if (item instanceof PlateDTO) {
            return ((PlateDTO) item).getId();
        }
        return null;
    }

    private Map<Long, String> mapPackageNamesById(List<PackageDTO> packages) {
        return packages.stream()
                .collect(Collectors.toMap(PackageDTO::getId, PackageDTO::getName));
    }

    private Map<Long, String> mapPackageImagesById(List<PackageDTO> packages) {
        return packages.stream()
                .filter(packageDTO -> packageDTO.getPhoto() != null)
                .collect(Collectors.toMap(PackageDTO::getId, this::getImageUrl));
    }

    private String getImageUrl(PackageDTO packageDTO) {
        byte[] imageBytes = imageService.getImage(packageDTO.getPhoto());
        if (imageBytes != null) {
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
        }
        return null; // Return null if the image is not available
    }
}

