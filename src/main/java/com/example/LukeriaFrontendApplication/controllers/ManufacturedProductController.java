package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ImageClient;
import com.example.LukeriaFrontendApplication.config.ManufacturedProductsClient;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.config.ProductClient;
import com.example.LukeriaFrontendApplication.dtos.ManufacturedProductDTO;
import com.example.LukeriaFrontendApplication.dtos.ManufacturedProductHelperDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/manufactured-product")
public class ManufacturedProductController {

    private final ManufacturedProductsClient manufacturedProductsClient;
    private final ProductClient productClient;
    private final PackageClient packageClient;
    private final ImageClient imageService;
    private static final String S3bucketImagesLink = "https://lukeria-images.s3.eu-central-1.amazonaws.com";

    @GetMapping("/all")
    public String allManufacturedProducts(Model model, HttpServletRequest request){
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<ManufacturedProductDTO> manufacturedProductDTOS = manufacturedProductsClient.getAllManufacturedProducts(token);
        List<ManufacturedProductHelperDTO> producedProducts = new ArrayList<>();

        for (ManufacturedProductDTO manufacturedProductDTO : manufacturedProductDTOS){
            ProductDTO productDTO = productClient.getProductById(manufacturedProductDTO.getProductId(),token);
            ManufacturedProductHelperDTO producedProduct = new ManufacturedProductHelperDTO();
            producedProduct.setId(manufacturedProductDTO.getId());
            producedProduct.setProduct(productDTO);
            producedProduct.setQuantity(manufacturedProductDTO.getQuantity());
            producedProduct.setManufactureDate(manufacturedProductDTO.getManufactureDate().toLocalDate());

            producedProducts.add(producedProduct);
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

        model.addAttribute("producedProducts", producedProducts);
        model.addAttribute("productPackageMapImages", productPackageMapImages);
        model.addAttribute("S3bucketImagesLink", S3bucketImagesLink);
        model.addAttribute("packages", packages);
        model.addAttribute("productPackageMap", productPackageMap);

        return "ManufacturedProducts/all-produced-products";
    }

}
