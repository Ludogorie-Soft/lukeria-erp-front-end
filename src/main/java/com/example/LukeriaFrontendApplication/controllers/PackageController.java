package com.example.LukeriaFrontendApplication.controllers;


import com.example.LukeriaFrontendApplication.config.CartonClient;
import com.example.LukeriaFrontendApplication.config.ImageService;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.config.PlateClient;
import com.example.LukeriaFrontendApplication.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
public class PackageController {
    private final PackageClient packageClient;
    private final CartonClient cartonClient;
    private final PlateClient plateClient;
    @Value("${backend.base-url}")
    private String backendBaseUrl;
    private final ImageService imageService;
    private static final String REDIRECTTXT = "redirect:/package/show";

    @GetMapping("/package/show")
    public String showPackage(Model model) {
        List<PackageDTO> packages = packageClient.getAllPackages();
        for (PackageDTO aPackage: packages) {
            imageService.getImage(aPackage.getPhoto());
        }
        model.addAttribute("packages", packages);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        return "Package/show";
    }

    @GetMapping("/package/create")
    String createPackage(Model model) {
        PackageDTO packageEntity = new PackageDTO();
        List<CartonDTO> cartons = cartonClient.getAllCartons();
        List<PlateDTO> plates = plateClient.getAllPlates();
        model.addAttribute("plates", plates);
        model.addAttribute("cartons", cartons);
        model.addAttribute("packageEntity", packageEntity);
        return "Package/create";
    }

    @PostMapping("/package/submit")
    public ModelAndView submitPackage(@ModelAttribute("packageEntity") PackageDTO packageDTO, @RequestParam MultipartFile file) throws IOException {
        packageClient.createPackage(packageDTO);
        if (!file.isEmpty()) {
            imageService.uploadImage(file);
        }
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/package/editPackage/{id}")
    String editPackage(@PathVariable(name = "id") Long id, Model model) {
        PackageDTO existingPackage = packageClient.getPackageById(id);
        List<CartonDTO> cartons = cartonClient.getAllCartons();
        List<PlateDTO> plates = plateClient.getAllPlates();
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
