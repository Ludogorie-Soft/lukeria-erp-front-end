package com.example.LukeriaFrontendApplication.controllers;


import com.example.LukeriaFrontendApplication.config.CartonClient;
import com.example.LukeriaFrontendApplication.config.ImageClient;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.config.PlateClient;
import com.example.LukeriaFrontendApplication.dtos.CartonDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.PlateDTO;
import jakarta.servlet.http.HttpServletRequest;
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
    private final ImageClient imageService;
    private static final String REDIRECTTXT = "redirect:/package/show";

    @GetMapping("/package/show")
    public String showPackage(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        for (PackageDTO aPackage : packages) {
            if (aPackage.getPhoto() != null) {
                imageService.getImage(aPackage.getPhoto());
            }
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
    public ModelAndView submitPackage(@ModelAttribute("packageEntity") PackageDTO packageDTO, @RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        packageClient.createPackage(packageDTO, token);
        if (!file.isEmpty()) {
            imageService.uploadImageForPackage(file);
        }
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/package/editPackage/{id}")
    String editPackage(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        PackageDTO existingPackage = packageClient.getPackageById(id, token);
        List<CartonDTO> cartons = cartonClient.getAllCartons();
        List<PlateDTO> plates = plateClient.getAllPlates();
        model.addAttribute("plates", plates);
        model.addAttribute("cartons", cartons);
        model.addAttribute("package", existingPackage);
        return "Package/edit";
    }

    @PostMapping("/package/editSubmit/{id}")
    ModelAndView editPackage(@PathVariable(name = "id") Long id, PackageDTO packageDTO, @RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        packageClient.updatePackage(id, packageDTO, token);
        if (!file.isEmpty()) {
            imageService.editImageForPackage(file, id);
        }
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/package/delete/{id}")
    ModelAndView deletePackageById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        packageClient.deletePackageById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }
}
