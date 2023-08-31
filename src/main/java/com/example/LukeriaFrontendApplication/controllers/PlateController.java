package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ImageClient;
import com.example.LukeriaFrontendApplication.config.PlateClient;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.PlateDTO;
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
@RequestMapping("/plate")
public class PlateController {
    private final PlateClient plateClient;
    private final ImageClient imageService;
    @Value("${backend.base-url}")
    private String backendBaseUrl;
    private  static final String CARTONTXT = "plate";
    private static final String REDIRECTTXT = "redirect:/plate/show";

    @GetMapping("/show")
    public String index(Model model) {
        List<PlateDTO> plates = plateClient.getAllPlates();
        for (PlateDTO plate : plates) {
            if(plate.getPhoto()!=null) {
                imageService.getImage(plate.getPhoto());
            }
        }
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("deleteMessageBoolean", true);
        model.addAttribute("plates", plates);
        return "Plate/show";
    }

    @GetMapping("/show/{id}")
    String getPlateById(@PathVariable(name = "id") Long id, Model model) {
        PlateDTO plate = plateClient.getPlateById(id);
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
    String editPlate(@PathVariable(name = "id") Long id, Model model) {
        PlateDTO existingPlate = plateClient.getPlateById(id);
        model.addAttribute(CARTONTXT, existingPlate);
        return "Plate/edit";
    }
    @PostMapping("/edit/{id}")
    ModelAndView editPlate(@PathVariable(name = "id") Long id, PlateDTO plateDTO, @RequestParam("file") MultipartFile file) throws IOException {
        plateClient.updatePlate(id, plateDTO);
        if (!file.isEmpty()) {
            imageService.editImageForPlate(file, id);
        }
        return new ModelAndView(REDIRECTTXT);
    }
    @PostMapping("/submit")
    public ModelAndView submitPlate(@ModelAttribute("plate") PlateDTO plateDTO, @RequestParam MultipartFile file) throws IOException {
        plateClient.createPlate(plateDTO);
        if (!file.isEmpty()) {
            imageService.uploadImageForPlate(file);
        }
        return new ModelAndView(REDIRECTTXT);
    }
    @PostMapping("/delete/{id}")
    ModelAndView deletePlateById(@PathVariable("id") Long id, Model model) {
        plateClient.deletePlateById(id);
        return new ModelAndView(REDIRECTTXT);
    }

}
