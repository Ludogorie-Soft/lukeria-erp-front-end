package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.ClientUserDTO;
import com.example.LukeriaFrontendApplication.dtos.CustomerCustomPriceDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductPriceDTO;
import com.example.LukeriaFrontendApplication.dtos.UserDTO;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product")
public class ProductController {
    private static final String REDIRECTTXT = "redirect:/product/show";
    private static final String S3bucketImagesLink = "https://lukeria-images.s3.eu-central-1.amazonaws.com";
    private static final String SESSION_TOKEN = "sessionToken";
    private final ProductClient productClient;
    private final PackageClient packageClient;
    private final ImageClient imageService;
    private final UserClient userClient;
    private final CustomerCustomPriceClient customerCustomPriceClient;
    private final ClientUserClient clientUserClient;
    private final CartonClient cartonClient;
    @Value("${backend.base-url}/images")
    private String backendBaseUrl;

    public void modelAtributes(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);

        List<PackageDTO> packages = packageClient.getAllPackages(token);

        Map<Long, String> productPackageMap = new HashMap<>();
        for (PackageDTO packageDTO : packages) {
            productPackageMap.put(packageDTO.getId(), packageDTO.getName());
        }

        Map<Long, String> productPackageMapImages = new HashMap<>();

        for (PackageDTO packageDTO : packages) {
            if (packageDTO.getPhoto() != null) {
                String imageUrl = S3bucketImagesLink + "/" + packageDTO.getPhoto();
                productPackageMapImages.put(packageDTO.getId(), imageUrl);
            }
        }


        model.addAttribute("productPackageMapImages", productPackageMapImages);
        //model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("packages", packages);
        model.addAttribute("productPackageMap", productPackageMap);
    }

    @GetMapping("/show")
    public String showAllProducts(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);

        List<ProductDTO> sortedProducts = productClient.getAllProducts(token).stream()
                .sorted(Comparator.comparingInt(ProductDTO::getAvailableQuantity).reversed())
                .toList();
        model.addAttribute("products", sortedProducts);
        modelAtributes(model, request);
        return "Product/show";
    }

    @GetMapping("/create")
    public String createProduct(Model model, HttpServletRequest request) {
        modelAtributes(model, request);
        ProductDTO product = new ProductDTO();
        model.addAttribute("product", product);
        return "Product/create";
    }

    @GetMapping("/available-products")
    public String showAvailableProductsForSale(Model model, HttpServletRequest request) {
        List<ProductPriceDTO> allProductsForSale = new ArrayList<>();

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<ProductDTO> productsForSale = productClient.getAvailableProducts(token);

        UserDTO authenticatedUser = userClient.findAuthenticatedUser(token);

        List<ClientUserDTO> userClientDtoList = clientUserClient.getAllClientUsers(token);

        Long clientId = userClientDtoList.stream()
                .filter(clientUserDTO -> clientUserDTO.getUserId().equals(authenticatedUser.getId()))  // Filter by userId instead of clientId
                .map(ClientUserDTO::getClientId)  // Map to clientId instead of userId
                .findFirst().orElse(null);


        List<ProductDTO> sortedProducts = productsForSale.stream()
                .sorted(Comparator.comparingInt(ProductDTO::getAvailableQuantity).reversed())
                .toList();

        for (ProductDTO productDTO : sortedProducts) {
            try {
                if (clientId == null) {
                    allProductsForSale.add(new ProductPriceDTO(productDTO, productDTO.getPrice()));
                } else {
                    CustomerCustomPriceDTO customPriceForClient = customerCustomPriceClient.customPriceByClientAndProduct(clientId, productDTO.getId(), token);
                    allProductsForSale.add(new ProductPriceDTO(productDTO, customPriceForClient.getPrice()));
                }
            } catch (FeignException.NotFound e) {
                allProductsForSale.add(new ProductPriceDTO(productDTO, productDTO.getPrice()));
            }
        }

        modelAtributes(model, request);
        model.addAttribute("products", allProductsForSale);
        return "Product/available-products";
    }

    @GetMapping("/for-sale")
    public String showProductsForSale(Model model, HttpServletRequest request) {
        List<ProductPriceDTO> allProductsForSale = new ArrayList<>();

        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<ProductDTO> productsForSale = productClient.getProductsForSaleWithoutLookingForQuantity(token);

        UserDTO authenticatedUser = userClient.findAuthenticatedUser(token);

        List<ClientUserDTO> userClientDtoList = clientUserClient.getAllClientUsers(token);

        Long clientId = userClientDtoList.stream()
                .filter(clientUserDTO -> clientUserDTO.getUserId().equals(authenticatedUser.getId()))  // Filter by userId instead of clientId
                .map(ClientUserDTO::getClientId)  // Map to clientId instead of userId
                .findFirst().orElse(null);


        List<ProductDTO> sortedProducts = productsForSale.stream()
                .sorted(Comparator.comparingInt(ProductDTO::getAvailableQuantity).reversed())
                .toList();

        for (ProductDTO productDTO : sortedProducts) {
            try {
                if (clientId == null) {
                    allProductsForSale.add(new ProductPriceDTO(productDTO, productDTO.getPrice()));
                } else {
                    CustomerCustomPriceDTO customPriceForClient = customerCustomPriceClient.customPriceByClientAndProduct(clientId, productDTO.getId(), token);
                    allProductsForSale.add(new ProductPriceDTO(productDTO, customPriceForClient.getPrice()));
                }
            } catch (FeignException.NotFound e) {
                // If NotFoundException is thrown, use the product's default price
                allProductsForSale.add(new ProductPriceDTO(productDTO, productDTO.getPrice()));
            }
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
        Map<ProductDTO, Integer> mapProductsAndPieces = new HashMap<>();
        for (int i = 0; i < productsForSale.size(); i++) {
            mapProductsAndPieces.put(productsForSale.get(i), packageClient.getPackageById(productsForSale.get(i).getPackageId(), token).getPiecesCarton());
        }
        model.addAttribute("mapProductsAndPieces", mapProductsAndPieces); // Add the map to the model
        model.addAttribute("productPackageMapImages", productPackageMapImages);
        model.addAttribute("backendBaseUrl", S3bucketImagesLink);
        model.addAttribute("products", allProductsForSale);
        model.addAttribute("packages", packages);
        model.addAttribute("productPackageMap", productPackageMap);
        return "Product/for-sale";
    }


    @PostMapping("/submit")
    public ModelAndView submitProduct(@ModelAttribute("product") ProductDTO productDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        productClient.createProduct(productDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteProductById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        productClient.deleteProductById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/editProduct/{id}")
    String editProduct(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        ProductDTO existingProduct = productClient.getProductById(id, token);
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        model.addAttribute("packages", packages);
        model.addAttribute("product", existingProduct);
        return "Product/edit";
    }

    @PostMapping("/editSubmit/{id}")
    ModelAndView editPackage(@PathVariable(name = "id") Long id, ProductDTO productDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        productClient.updateProduct(id, productDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/produce")
    public String produceProduct(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<ProductDTO> products = productClient.getAllProducts(token);
        List<PackageDTO> packages = packageClient.getAllPackages(token);

        Map<Long, String> productPackageMap = new HashMap<>();
        for (PackageDTO packageDTO : packages) {
            productPackageMap.put(packageDTO.getId(), packageDTO.getName());
        }

        Map<Long, String> productPackageMapImages = new HashMap<>();
        for (PackageDTO packageDTO : packages) {
            if (packageDTO.getPhoto() != null) {
                String imageUrl = S3bucketImagesLink + "/" + packageDTO.getPhoto();
                productPackageMapImages.put(packageDTO.getId(), imageUrl);
            }
        }


        List<Long> emptyProductIdList = new ArrayList<>();
        List<Integer> emptyQuantityList = new ArrayList<>();
        model.addAttribute("emptyProductIdList", emptyProductIdList);
        model.addAttribute("emptyQuantityList", emptyQuantityList);
        model.addAttribute("products", products);
        model.addAttribute("backendBaseUrl", S3bucketImagesLink);
        model.addAttribute("productPackageMap", productPackageMap);
        model.addAttribute("productPackageMapImages", productPackageMapImages);
        return "Product/produce";
    }


    @PostMapping("/produce")
    public ModelAndView produceProduct(
            @RequestParam("productId[]") List<Long> productIds,
            @RequestParam("producedQuantity[]") List<Integer> producedQuantities,
            HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);

        for (int i = 0; i < productIds.size(); i++) {
            productClient.produceProduct(productIds.get(i), producedQuantities.get(i), token);
        }
        return new ModelAndView(REDIRECTTXT);
    }
}
