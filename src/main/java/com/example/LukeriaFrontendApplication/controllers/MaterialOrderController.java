package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/material-order")
public class MaterialOrderController {
    private static final String ORDERTXT = "orders";
    private static final String PLATETXT = "plates";
    private static final String PACKAGETXT = "packages";
    private static final String CARTONTXT = "cartons";
    private static final String PRODUCTTXT = "products";
    private static final String MATERIALTXT = "material";
    private static final String REDIRECTTXT = "redirect:/material-order/show";
    private static final String MATERIALSORDERSHOW = "MaterialOrder/show";
    private final MaterialOrderClient materialOrderClient;
    private final CartonClient cartonClient;
    private final PackageClient packageClient;
    private final PlateClient plateClient;
    private final ProductClient productClient;
    @Value("${backend.base-url}/images")
    private String backendBaseUrl;

    @GetMapping("/create")
    String createMaterialOrder(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        model.addAttribute(CARTONTXT, cartonClient.getAllCartons(token));
        model.addAttribute(PACKAGETXT, packageClient.getAllPackages(token));
        model.addAttribute(PLATETXT, plateClient.getAllPlates(token));
        model.addAttribute("order", materialOrderDTO);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        return "MaterialOrder/create";
    }

    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<MaterialOrderDTO> materialOrderDTOS = materialOrderClient.getAllMaterialOrders(token);
        Collections.reverse(materialOrderDTOS);
        model.addAttribute(ORDERTXT, materialOrderDTOS);
        return MATERIALSORDERSHOW;
    }

    @GetMapping("/materials/{id}")
    public String showMaterialForOrderId(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<MaterialOrderDTO> materialsForOrder = materialOrderClient.getAllProductsByOrderId(id, token);
        if (materialsForOrder.isEmpty()) {
            model.addAttribute("materialAvailability", true);
            model.addAttribute("materialsForOrder", materialsForOrder);
            model.addAttribute("message", "Всички материални са налични!");
        }
        List<ProductDTO> products=productClient.getAllProducts(token);
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        List<CartonDTO> cartons = cartonClient.getAllCartons(token);
        List<PlateDTO> plates = plateClient.getAllPlates(token);

        model.addAttribute(PRODUCTTXT,products);
        model.addAttribute(PACKAGETXT, packages);
        model.addAttribute(CARTONTXT, cartons);
        model.addAttribute(PLATETXT, plates);
        model.addAttribute(ORDERTXT, materialsForOrder);
        return "MaterialOrder/showMaterialForOrderId";
    }

    @GetMapping("/all-materials")
    public String showMaterialForAllOrders(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<MaterialOrderDTO> materialsForOrder = materialOrderClient.allAvailableProducts(token);
        if (materialsForOrder.isEmpty()) {
            model.addAttribute("materialAvailability", true);
            model.addAttribute("materialsForOrder", materialsForOrder);
            model.addAttribute("message", "Всички материални са налични!");
        }
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        List<CartonDTO> cartons = cartonClient.getAllCartons(token);
        List<PlateDTO> plates = plateClient.getAllPlates(token);
        List<ProductDTO> products=productClient.getAllProducts(token);

        model.addAttribute(PRODUCTTXT,products);
        model.addAttribute(PACKAGETXT, packages);
        model.addAttribute(CARTONTXT, cartons);
        model.addAttribute(PLATETXT, plates);
        model.addAttribute(ORDERTXT, materialsForOrder);
        return "MaterialOrder/showMaterialForOrderId";
    }

    @PostMapping("/submit")
    public ModelAndView submitMaterialOrder(@ModelAttribute("order") MaterialOrderDTO materialOrderDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        materialOrderClient.createMaterialOrder(materialOrderDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteMaterialOrderById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        materialOrderClient.deleteMaterialOrderById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/edit/{id}")
    String editMaterialOrder(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        MaterialOrderDTO existingOrder = materialOrderClient.getMaterialOrderById(id, token);
        model.addAttribute(CARTONTXT, cartonClient.getAllCartons(token));
        model.addAttribute(PACKAGETXT, packageClient.getAllPackages(token));
        model.addAttribute(PLATETXT, plateClient.getAllPlates(token));
        model.addAttribute("order", existingOrder);
        return "MaterialOrder/edit";
    }

    @PostMapping("/editSubmit/{id}")
    ModelAndView editMaterialOrder(@PathVariable(name = "id") Long id, MaterialOrderDTO materialOrderDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        materialOrderClient.updateMaterialOrder(id, materialOrderDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/material/{materialId}")
    public String viewMaterial(@PathVariable Long materialId, @RequestParam("materialType") String materialType, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        if (materialType.equals("CARTON")) {
            model.addAttribute(MATERIALTXT, cartonClient.getCartonById(materialId, token));
            model.addAttribute("type", "Кашон");
        } else if (materialType.equals("PLATE")) {
            model.addAttribute(MATERIALTXT, plateClient.getPlateById(materialId, token));
            model.addAttribute("type", "Тарелка");
        } else {
            PackageDTO packageDTO = packageClient.getPackageById(materialId, token);
            model.addAttribute(MATERIALTXT, packageDTO);
            model.addAttribute("productCode", packageDTO.getProductCode());
            model.addAttribute("type", "Кутия");
        }
        return "MaterialOrder/material";
    }
}
