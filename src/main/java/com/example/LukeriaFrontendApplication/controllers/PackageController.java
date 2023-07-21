package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.CartonClient;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.config.PlateClient;
import com.example.LukeriaFrontendApplication.dtos.CartonDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.PlateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
public class PackageController {
    private final PackageClient packageClient;
    private final CartonClient cartonClient;
    private final PlateClient plateClient;
    private static final String REDIRECTTXT ="redirect:/package/show";


    @GetMapping("/package/show")
    public String showPackage(Model model) {
        List<PackageDTO> packages = packageClient.getAllPackages();
        model.addAttribute("packages", packages);
        return "Package/show";
    }

    @GetMapping("/package/create")
    String createPackage(Model model) {
        PackageDTO packageEntity = new PackageDTO();
        List<CartonDTO> cartons=cartonClient.getAllCartons();
        List<PlateDTO> plates=plateClient.getAllPlates();
        model.addAttribute("plates", plates);
        model.addAttribute("cartons", cartons);
        model.addAttribute("packageEntity", packageEntity);
        return "Package/create";
    }

    @PostMapping("/package/submit")
    public ModelAndView submitPackage(@ModelAttribute("packageEntity") PackageDTO packageDTO) {
        packageClient.createPackage(packageDTO);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/package/editPackage/{id}")
    String editPackage(@PathVariable(name = "id") Long id, Model model) {
        PackageDTO existingPackage = packageClient.getPackageById(id);
        List<CartonDTO> cartons=cartonClient.getAllCartons();
        List<PlateDTO> plates=plateClient.getAllPlates();
        model.addAttribute("plates", plates);
        model.addAttribute("cartons", cartons);
        model.addAttribute("package", existingPackage);
        return "Package/edit";
    }

    @PostMapping("/package/editSubmit/{id}")
    ModelAndView editPackage(@PathVariable(name = "id") Long id, PackageDTO packageDTO) {
        packageClient.updatePackage(id, packageDTO);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/package/delete/{id}")
    ModelAndView deletePackageById(@PathVariable("id") Long id, Model model) {
        packageClient.deletePackageById(id);
        return new ModelAndView(REDIRECTTXT);
    }
}
