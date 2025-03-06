package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.CartonClient;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.dtos.ManufacturedProductDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/manufactured-product")
public class ManufacturedProductController {

    private final CartonClient cartonClient;
    private final PackageClient packageClient;
    private static final String S3bucketImagesLink = "https://lukeria-images.s3.eu-central-1.amazonaws.com";

    @GetMapping("/show")
    public String allManufacturedProducts(Model model, HttpServletRequest request){
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<ManufacturedProductDTO> producedProducts = cartonClient.getAllManufacturedProducts(token);

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

        model.addAttribute("producedProducts", producedProducts);
        model.addAttribute("productPackageMapImages", productPackageMapImages);
        model.addAttribute("S3bucketImagesLink", S3bucketImagesLink);
        model.addAttribute("packages", packages);
        model.addAttribute("productPackageMap", productPackageMap);

        return "ManufacturedProducts/all";
    }

}
