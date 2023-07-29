package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.CartonClient;
import com.example.LukeriaFrontendApplication.config.MaterialOrderClient;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.config.PlateClient;
import com.example.LukeriaFrontendApplication.dtos.CartonDTO;
import com.example.LukeriaFrontendApplication.dtos.MaterialOrderDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.PlateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/material-order")
public class MaterialOrderController {
    private final MaterialOrderClient materialOrderClient;
    private final CartonClient cartonClient;
    private final PackageClient packageClient;
    private final PlateClient plateClient;
    private static final String ORDERTXT = "order";
    private static final String REDIRECTTXT = "redirect:/material-order/show";

    @GetMapping("/create")
    String createMaterialOrder(Model model) {
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        model.addAttribute("cartons", cartonClient.getAllCartons());
        model.addAttribute("packages", packageClient.getAllPackages());
        model.addAttribute("plates", plateClient.getAllPlates());
        model.addAttribute(ORDERTXT, materialOrderDTO);
        return "MaterialOrder/create";
    }
    @GetMapping("/show")
    public String index(Model model) {
        List<MaterialOrderDTO> materialOrderDTOS = materialOrderClient.getAllMaterialOrders();
        model.addAttribute("orders", materialOrderDTOS);
        return "MaterialOrder/show";
    }

    @GetMapping("/materials/{id}")
    public String showMaterialForOrderId(@PathVariable("id") Long id,Model model) {
        List<MaterialOrderDTO> materialsForOrder = materialOrderClient.getAllProductsByOrderId(id);
        List<PackageDTO> packages=packageClient.getAllPackages();
        List<CartonDTO> cartons =cartonClient.getAllCartons();
        List<PlateDTO> plates=plateClient.getAllPlates();

        model.addAttribute("packages", packages);
        model.addAttribute("cartons", cartons);
        model.addAttribute("plates", plates);
        model.addAttribute("orders", materialsForOrder);
        return "MaterialOrder/showMaterialForOrderId";
    }

    @GetMapping("/all-materials")
    public String showMaterialForAllOrders(Model model) {
        List<MaterialOrderDTO> materialsForOrder = materialOrderClient.allAvailableProducts();
        List<PackageDTO> packages=packageClient.getAllPackages();
        List<CartonDTO> cartons =cartonClient.getAllCartons();
        List<PlateDTO> plates=plateClient.getAllPlates();

        model.addAttribute("packages", packages);
        model.addAttribute("cartons", cartons);
        model.addAttribute("plates", plates);
        model.addAttribute("orders", materialsForOrder);
        return "MaterialOrder/showMaterialForOrderId";
    }

    @PostMapping("/submit")
    public ModelAndView submitMaterialOrder(@ModelAttribute("order") MaterialOrderDTO materialOrderDTO) {
        materialOrderClient.createMaterialOrder(materialOrderDTO);
        return new ModelAndView("redirect:/index");
    }
    @PostMapping("/delete/{id}")
    ModelAndView deleteMaterialOrderById(@PathVariable("id") Long id, Model model) {
        materialOrderClient.deleteMaterialOrderById(id);
        return new ModelAndView(REDIRECTTXT);
    }
    @GetMapping("/edit/{id}")
    String editMaterialOrder(@PathVariable(name = "id") Long id, Model model) {
        MaterialOrderDTO existingOrder = materialOrderClient.getMaterialOrderById(id);
        model.addAttribute("cartons", cartonClient.getAllCartons());
        model.addAttribute("packages", packageClient.getAllPackages());
        model.addAttribute("plates", plateClient.getAllPlates());
        model.addAttribute(ORDERTXT, existingOrder);
        return "MaterialOrder/edit";
    }
    @PostMapping("/editSubmit/{id}")
    ModelAndView editMaterialOrder(@PathVariable(name = "id") Long id, MaterialOrderDTO materialOrderDTO) {
        materialOrderClient.updateMaterialOrder(id, materialOrderDTO);
        return new ModelAndView(REDIRECTTXT);
    }
}
