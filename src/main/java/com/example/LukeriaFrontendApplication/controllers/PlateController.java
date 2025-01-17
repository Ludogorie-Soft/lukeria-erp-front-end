package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ImageClient;
import com.example.LukeriaFrontendApplication.config.PlateClient;
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
import java.util.*;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/plate")
public class PlateController {
    private static final String CARTONTXT = "plate";
    private static final String REDIRECTTXT = "redirect:/plate/show";
    private static final String S3bucketImagesLink = "https://lukeria-images.s3.eu-central-1.amazonaws.com";

    private final PlateClient plateClient;
    private final ImageClient imageService;
    @Value("${backend.base-url}/images")
    private String backendBaseUrl;

    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<PlateDTO> plates = plateClient.getAllPlates(token);

        Map<Long, String> plateImages = new HashMap<>();

        for (PlateDTO plate : plates) {
            if (plate.getPhoto() != null) {
                String imageUrl = S3bucketImagesLink + "/" + plate.getPhoto();
                plateImages.put(plate.getId(), imageUrl);
            }
        }

        List<PlateDTO> sortedPlates = plates.stream()
                .sorted(Comparator.comparingInt(PlateDTO::getAvailableQuantity).reversed())
                .toList();

        model.addAttribute("plates", sortedPlates);
        model.addAttribute("plateImages", plateImages);
        model.addAttribute("backendBaseUrl", S3bucketImagesLink);

        return "Plate/show";
    }

    @GetMapping("/show/{id}")
    String getPlateById(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        PlateDTO plate = plateClient.getPlateById(id, token);
        model.addAttribute(CARTONTXT, plate);
        return "Plate/showById";
    }


    @GetMapping("/create")
    String createPlate(Model model) {
        PlateDTO plate = new PlateDTO();
        model.addAttribute(CARTONTXT, plate);
        return "Plate/create";
    }

    @GetMapping("/editPlate/{id}")
    String editPlate(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        PlateDTO existingPlate = plateClient.getPlateById(id, token);

        String imageUrl = existingPlate.getPhoto() != null ? S3bucketImagesLink + "/" + existingPlate.getPhoto() : null;

        model.addAttribute(CARTONTXT, existingPlate);
        model.addAttribute("image", imageUrl);
        return "Plate/edit";
    }

    @PostMapping("/edit/{id}")
    ModelAndView editPlate(@PathVariable(name = "id") Long id, PlateDTO plateDTO, @RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        String token = (String) request.getSession().getAttribute("sessionToken");
        plateClient.updatePlate(id, plateDTO, token);
        if (!file.isEmpty()) {
            imageService.editImageForPlate(file, id);
        }
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/submit")
    public ModelAndView submitPlate(@ModelAttribute("plate") PlateDTO plateDTO, @RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        String token = (String) request.getSession().getAttribute("sessionToken");
        plateClient.createPlate(plateDTO, token);
        if (!file.isEmpty()) {
            imageService.uploadImageForPlate(file);
        }
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deletePlateById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        plateClient.deletePlateById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }

}
