package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.*;
import com.example.LukeriaFrontendApplication.enums.MaterialType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private static final String S3bucketImagesLink = "https://lukeria-images.s3.eu-central-1.amazonaws.com";

    private static final String MATERIALSORDERSHOW = "MaterialOrder/show";
    private final MaterialOrderClient materialOrderClient;
    private final CartonClient cartonClient;
    private final PackageClient packageClient;
    private final PlateClient plateClient;
    private final ProductClient productClient;

    @GetMapping("/create")
    String createMaterialOrder(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        model.addAttribute("cartons", cartonClient.getAllCartons(token));
        model.addAttribute("packages", packageClient.getAllPackages(token));
        model.addAttribute("plates", plateClient.getAllPlates(token));
        model.addAttribute("order", new MaterialOrderDTO());
        model.addAttribute("S3bucketImagesLink", S3bucketImagesLink);
        return "MaterialOrder/create";
    }

    @GetMapping("/item/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        // Fetch all material order items
        List<MaterialOrderItemDTO> materialOrderItemDTOS = materialOrderClient.getAllMaterialOrderItems(token);

        for (MaterialOrderItemDTO item : materialOrderItemDTOS) {
            if (item.getId().equals(id)) {
                model.addAttribute("materialOrderItem", item);
                model.addAttribute("cartons", cartonClient.getAllCartons(token));
                model.addAttribute("packages", packageClient.getAllPackages(token));
                model.addAttribute("plates", plateClient.getAllPlates(token));
                model.addAttribute("S3bucketImagesLink", S3bucketImagesLink);
                return "MaterialOrder/item/edit";
            }
        }
        return "redirect:/material-order/show";
    }

    @PostMapping("/item/update")
    public String updateMaterialOrderItem(
            @ModelAttribute("materialOrderItem") @Valid MaterialOrderItemDTO materialOrderItemDTO,
            BindingResult bindingResult, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String token = (String) request.getSession().getAttribute("sessionToken");
//        if (materialOrderItemDTO.getId() == null) {
//     throw new NoSuchElementException("Material order item not found.");
//        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.materialOrderItem", bindingResult);
            redirectAttributes.addFlashAttribute("materialOrderItem", materialOrderItemDTO);
            return "redirect:/material-order/edit/" + materialOrderItemDTO.getId(); // Redirect back to edit form
        }

//        try {
        materialOrderClient.updateMaterialOrderItem(materialOrderItemDTO, token);
        redirectAttributes.addFlashAttribute("successMessage", "Материалът беше успешно актуализиран.");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Грешка при обновяването на материала.");
//        }

        return "redirect:/material-order/show";
    }


//    @PostMapping("/{id}/update")
//    public String updateMaterialOrderItem(@PathVariable Long id, @ModelAttribute MaterialOrderItemDTO itemDTO) {
//        materialOrderItemService.updateItem(id, itemDTO);
//        return "redirect:/material-order/show/" + itemDTO.getOrderId();
//    }

    //    @GetMapping("/show")
//    public String index(Model model, HttpServletRequest request) {
//        String token = (String) request.getSession().getAttribute("sessionToken");
//        List<MaterialOrderDTO> materialOrderDTOS = materialOrderClient.getAllMaterialOrders(token);
//
//        for (MaterialOrderDTO materialOrder : materialOrderDTOS) {
//            MaterialOrderHelperForShowDTO materialOrderHelperForShowDTO = new MaterialOrderHelperForShowDTO();
//            Long materialId = materialOrder.getMaterialId();
//
//            if (materialOrder.getMaterialType().equalsIgnoreCase("Carton")) {
//                CartonDTO cartonDTO = cartonClient.getCartonById(materialId, token);
//                materialOrderHelperForShowDTO.setName(cartonDTO.getName());
//            }
//            if (materialOrder.getMaterialType().equalsIgnoreCase("Plate")) {
//                PlateDTO plateDTO = plateClient.getPlateById(materialId, token);
//                materialOrderHelperForShowDTO.setName(plateDTO.getName());
//            }
//            if (materialOrder.getMaterialType().equalsIgnoreCase("Package")) {
//                PackageDTO packageDTO = packageClient.getPackageById(materialId, token);
//                String packageString = packageDTO.getProductCode()+" - "+packageDTO.getName();
//                materialOrderHelperForShowDTO.setName(packageString);
//            }
//
//
//
//
//            materialOrderHelperForShowDTO.setId(materialOrder.getId());
//            materialOrderHelperForShowDTO.setMaterialType(materialOrder.getMaterialType());
//            materialOrderHelperForShowDTO.setOrderedQuantity(materialOrder.getOrderedQuantity());
//            materialOrderHelperForShowDTO.setMaterialId(materialId);
//            materialOrderHelperForShowDTO.setReceivedQuantity(materialOrder.getReceivedQuantity());
//            materialOrderHelperForShowDTO.setArrivalDate(materialOrder.getArrivalDate());
//            materialOrderHelperForShowDTO.setMaterialPrice(materialOrder.getMaterialPrice());
//            forShow.add(materialOrderHelperForShowDTO);
//        }
//
//        Collections.reverse(forShow);
//        model.addAttribute(ORDERTXT, forShow);
//        return MATERIALSORDERSHOW;
//    }
    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<MaterialOrderDTO> materialOrders = materialOrderClient.getAllMaterialOrders(token);

        materialOrders.forEach(order ->
                order.getItems().forEach(item -> {
                    switch (item.getMaterialType()) {
                        case CARTON:
                            CartonDTO carton = cartonClient.getCartonById(item.getMaterialId(), token);
                            item.setMaterialName(carton.getName());
                            break;
                        case PACKAGE:
                            PackageDTO packageDTO = packageClient.getPackageById(item.getMaterialId(), token);
                            if (packageDTO.getPhoto() != null) {
                                item.setPhoto(S3bucketImagesLink + "/" + packageDTO.getPhoto()); // ✅ Set full URL
                            }
                            break;
                        case PLATE:
                            PlateDTO plate = plateClient.getPlateById(item.getMaterialId(), token);
                            item.setMaterialName(plate.getName());
                            if (plate.getPhoto() != null) {
                                item.setPhoto(S3bucketImagesLink + "/" + plate.getPhoto()); // ✅ Set full URL
                            }
                            break;
                    }
                })
        );

        List<PackageDTO> packages = packageClient.getAllPackages(token);
        Map<Long, PackageDTO> packageMap = packages.stream()
                .collect(Collectors.toMap(PackageDTO::getId, Function.identity()));

        model.addAttribute("packageMap", packageMap); // Добавяме Map-а в Thymeleaf
        List<PlateDTO> plates = plateClient.getAllPlates(token);
        model.addAttribute("packages", packages);
        model.addAttribute("plates", plates);
        model.addAttribute("materialOrders", materialOrders);
        model.addAttribute("MaterialTypes", MaterialType.values());
        model.addAttribute("S3bucketImagesLink", S3bucketImagesLink);
        return "MaterialOrder/show";
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
        List<ProductDTO> products = productClient.getAllProducts(token);
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        List<CartonDTO> cartons = cartonClient.getAllCartons(token);
        List<PlateDTO> plates = plateClient.getAllPlates(token);

        model.addAttribute(PRODUCTTXT, products);
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
        List<ProductDTO> products = productClient.getAllProducts(token);

        model.addAttribute(PRODUCTTXT, products);
        model.addAttribute(PACKAGETXT, packages);
        model.addAttribute(CARTONTXT, cartons);
        model.addAttribute(PLATETXT, plates);
        model.addAttribute(ORDERTXT, materialsForOrder);
        return "MaterialOrder/showMaterialForOrderId";
    }

    //    @PostMapping("/submit")
//    public ModelAndView submitMaterialOrder(@ModelAttribute("order") MaterialOrderDTO materialOrderDTO, HttpServletRequest request) {
//        String token = (String) request.getSession().getAttribute("sessionToken");
//        materialOrderClient.createMaterialOrder(materialOrderDTO, token);
//        return new ModelAndView(REDIRECTTXT);
//    }
    @PostMapping("/order-materials")
    public String submitMaterialOrder(@ModelAttribute MaterialOrderDTO order, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        for (int i = 0; i < order.getItems().size(); i++) {
            order.getItems().get(i).setReceivedQuantity(0);
        }

        try {
            materialOrderClient.submitMaterialOrder(order, token);
            redirectAttributes.addFlashAttribute("successMessage", "Поръчката е изпратена успешно!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Възникна грешка при изпращането на поръчката.");
        }

        return "redirect:/material-order/show";
    }


    @PostMapping("/delete/{id}")
    ModelAndView deleteMaterialOrderById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        materialOrderClient.deleteMaterialOrderById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }

//    @GetMapping("/edit/{id}")
//    String editMaterialOrder(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
//        String token = (String) request.getSession().getAttribute("sessionToken");
//        MaterialOrderDTO existingOrder = materialOrderClient.getMaterialOrderById(id, token);
//        model.addAttribute(CARTONTXT, cartonClient.getAllCartons(token));
//        model.addAttribute(PACKAGETXT, packageClient.getAllPackages(token));
//        model.addAttribute(PLATETXT, plateClient.getAllPlates(token));
//        model.addAttribute("order", existingOrder);
//        return "MaterialOrder/edit";
//    }

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
