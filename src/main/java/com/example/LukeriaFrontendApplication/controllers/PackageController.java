package com.example.LukeriaFrontendApplication.controllers;


import com.example.LukeriaFrontendApplication.config.*;
import com.example.LukeriaFrontendApplication.dtos.CartonDTO;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.PlateDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
public class PackageController {
    private static final String REDIRECTTXT = "redirect:/package/show";
    private static final String SESSION_TOKEN = "sessionToken";
    private final PackageClient packageClient;
    private final CartonClient cartonClient;
    private final ProductClient productClient;
    private final PlateClient plateClient;
    private final ImageClient imageService;
    @Value("${backend.base-url}/images")
    private String backendBaseUrl;

    @GetMapping("/package/show")
    public String showPackage(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<PackageDTO> packages = packageClient.getAllPackages(token);

        Map<Long, String> productPackageMapImages = new HashMap<>();

        for (PackageDTO packageDTO : packages) {
            if (packageDTO.getPhoto() != null) {
                String imageUrl = backendBaseUrl + "/" + packageDTO.getPhoto();
                productPackageMapImages.put(packageDTO.getId(), imageUrl);
            }
        }
        model.addAttribute("productPackageMapImages", productPackageMapImages);
        List<PackageDTO> sortedPackages = packages.stream()
                .sorted(Comparator.comparingInt(PackageDTO::getAvailableQuantity).reversed())
                .toList();
        model.addAttribute("packages", sortedPackages);
        return "Package/show";
    }

    @GetMapping("/package/{id}")
    public String packageInformation(@PathVariable Long id, Model model, HttpServletRequest request) {
        // Retrieve the token from the session
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);

        // Fetch the package, product, carton, and plate details
        ProductDTO selectedProduct;
        PackageDTO selectedPackage;
        CartonDTO selectedCarton;
        PlateDTO selectedPlate;
        try {
            selectedPackage = packageClient.getPackageById(id, token);
            selectedProduct = productClient.getProductByPackage(selectedPackage.getId(), token).getBody();
            selectedCarton = cartonClient.getCartonById(selectedPackage.getCartonId(), token);
            selectedPlate = plateClient.getPlateById(selectedPackage.getPlateId(), token);
        } catch (FeignException.NotFound e) {
            return "redirect:/package/show"; // Or a custom error page like "Package/error"
        }

        // Retrieve and encode the package's image, if it exists
        String packageImage = null;
        if (selectedPackage.getPhoto() != null) {
            byte[] imageBytes = imageService.getImage(selectedPackage.getPhoto());
            if (imageBytes != null) {
                packageImage = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
            }
        }

        // Retrieve and encode the plate's image, if it exists
        String plateImage = null;
        if (selectedPlate.getPhoto() != null) {
            byte[] imageBytes = imageService.getImage(selectedPlate.getPhoto());
            if (imageBytes != null) {
                plateImage = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
            }
        }

        // Add the selected package, product, carton, plate, and images to the model
        model.addAttribute("selectedPackage", selectedPackage);
        model.addAttribute("selectedProduct", selectedProduct);
        model.addAttribute("selectedCarton", selectedCarton);
        model.addAttribute("selectedPlate", selectedPlate);
        model.addAttribute("packageImage", packageImage);
        model.addAttribute("plateImage", plateImage);

        // Return the package details page
        return "Package/details"; // Ensure you create this template to display package, product, carton, and plate information
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

    @GetMapping("/package/materials/{id}")
    public ModelAndView getAllMaterialsForPackageById(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        packageClient.getAllMaterialsForPackageById(id, token);
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
