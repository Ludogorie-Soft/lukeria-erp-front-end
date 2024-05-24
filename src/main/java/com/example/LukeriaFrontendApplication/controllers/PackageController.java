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

import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
public class PackageController {
    private static final String REDIRECTTXT = "redirect:/package/show";
    private static final String SESSION_TOKEN = "sessionToken";
    private final PackageClient packageClient;
    private final CartonClient cartonClient;
    private final PlateClient plateClient;
    private final ImageClient imageService;
    @Value("${backend.base-url}/images")
    private String backendBaseUrl;

    @GetMapping("/package/show")
    public String showPackage(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<PackageDTO> packages = packageClient.getAllPackages(token);
        for (PackageDTO aPackage : packages) {
            if (aPackage.getPhoto() != null) {
                imageService.getImage(aPackage.getPhoto());
            }
        }
        List<PackageDTO> sortedPackage = packages.stream()
                .sorted(Comparator.comparingInt(PackageDTO::getAvailableQuantity).reversed())
                .toList();
        model.addAttribute("packages", sortedPackage);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        return "Package/show";
    }

    @GetMapping("/package/create")
    String createPackage(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        PackageDTO packageEntity = new PackageDTO();
        List<CartonDTO> cartons = cartonClient.getAllCartons(token);
        List<PlateDTO> plates = plateClient.getAllPlates(token);
        model.addAttribute("plates", plates);
        model.addAttribute("cartons", cartons);
        model.addAttribute("packageEntity", packageEntity);
        return "Package/create";
    }

    @PostMapping("/package/submit")
    public ModelAndView submitPackage(@ModelAttribute("packageEntity") PackageDTO packageDTO, @RequestParam MultipartFile file, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        packageClient.createPackage(packageDTO, token);
        if (!file.isEmpty()) {
            imageService.uploadImageForPackage(file);
        }
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/package/editPackage/{id}")
    public String editPackage(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        PackageDTO existingPackage = packageClient.getPackageById(id, token);
        List<CartonDTO> cartons = cartonClient.getAllCartons(token);
        List<PlateDTO> plates = plateClient.getAllPlates(token);

        byte[] imageBytes = existingPackage.getPhoto() != null ? imageService.getImage(existingPackage.getPhoto()) : null;
        String imageUrl = imageBytes != null ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes) : null;

        model.addAttribute("plates", plates);
        model.addAttribute("cartons", cartons);
        model.addAttribute("package", existingPackage);
        model.addAttribute("image", imageUrl);

        return "Package/edit";
    }

    @PostMapping("/package/editSubmit/{id}")
    ModelAndView editPackage(@PathVariable(name = "id") Long id, PackageDTO packageDTO, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        packageClient.updatePackage(id, packageDTO, token);
        if (!file.isEmpty()) {
            imageService.editImageForPackage(file, id);
        }
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/package/delete/{id}")
    ModelAndView deletePackageById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        packageClient.deletePackageById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/plate/{plateId}")
    public String getPlateInfo(@PathVariable Long plateId, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        model.addAttribute("plate", plateClient.getPlateById(plateId, token));
        return "Package/plateInfoPage";
    }

    @GetMapping("/carton/{cartonId}")
    public String getCartonInfo(@PathVariable Long cartonId, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        model.addAttribute("carton", cartonClient.getCartonById(cartonId, token));
        return "Package/cartonInfoPage";
    }
}
