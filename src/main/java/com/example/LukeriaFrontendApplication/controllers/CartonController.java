package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.CartonClient;
import com.example.LukeriaFrontendApplication.dtos.CartonDTO;
import jakarta.servlet.http.HttpServletRequest;
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
public class CartonController {
    private static final String CARTONTXT = "carton";
    private static final String REDIRECTTXT = "redirect:/show";
    private final CartonClient cartonClient;

    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<CartonDTO> cartons = cartonClient.getAllCartons(token);
        model.addAttribute("deleteMessageBoolean", true);
        model.addAttribute("cartons", cartons);
        return "Carton/show";
    }


    @GetMapping("/create")
    String createCarton(Model model) {
        CartonDTO carton = new CartonDTO();
        model.addAttribute(CARTONTXT, carton);
        return "Carton/create";
    }

    @GetMapping("/editCarton/{id}")
    String editCarton(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        CartonDTO existingCarton = cartonClient.getCartonById(id, token);
        model.addAttribute(CARTONTXT, existingCarton);
        return "Carton/edit";
    }

    @PostMapping("/submit")
    public ModelAndView submitCarton(@ModelAttribute("carton") CartonDTO cartonDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        cartonClient.createCarton(cartonDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/edit/{id}")
    ModelAndView editCarton(@PathVariable(name = "id") Long id, CartonDTO cartonDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        cartonClient.updateCarton(id, cartonDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteCartonById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        cartonClient.deleteCartonById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }

}
