package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.PlateClient;
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
@RequestMapping("/plate")
public class PlateController {
    private final PlateClient plateClient;
    private  static final String CARTONTXT = "plate";
    private static final String REDIRECTTXT = "redirect:/plate/show";

    @GetMapping("/show")
    public String index(Model model) {
        List<PlateDTO> plates = plateClient.getAllPlates();
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

    @PostMapping("/submit")
    public ModelAndView submitPlate(@ModelAttribute("plate") PlateDTO plateDTO) {
        plateClient.createPlate(plateDTO);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/edit/{id}")
    ModelAndView editPlate(@PathVariable(name = "id") Long id, PlateDTO plateDTO) {
        plateClient.updatePlate(id, plateDTO);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deletePlateById(@PathVariable("id") Long id, Model model) {
        plateClient.deletePlateById(id);
        return new ModelAndView(REDIRECTTXT);
    }

}
