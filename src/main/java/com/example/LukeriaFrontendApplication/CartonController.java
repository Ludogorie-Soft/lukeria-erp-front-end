package com.example.LukeriaFrontendApplication;

import com.example.LukeriaFrontendApplication.config.CartonClient;
import com.example.LukeriaFrontendApplication.dtos.CartonDTO;
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
    private final CartonClient cartonClient;
    private  static final String CARTONTXT = "carton";
    private static final String REDIRECTTXT = "redirect:/show";

    @GetMapping("/show")
    public String index(Model model) {
        List<CartonDTO> cartons = cartonClient.getAllCartons();
        model.addAttribute("deleteMessageBoolean", true);

        model.addAttribute("cartons", cartons);
        return "Carton/show";
    }

    @GetMapping("/show/{id}")
    String getCartonById(@PathVariable(name = "id") Long id, Model model) {
        CartonDTO carton = cartonClient.getCartonById(id);
        model.addAttribute(CARTONTXT, carton);
        return "Carton/showById";
    }


    @GetMapping("/create")
    String createCarton(Model model) {
        CartonDTO carton = new CartonDTO();
        model.addAttribute(CARTONTXT, carton);
        return "Carton/create";
    }

    @GetMapping("/editCarton/{id}")
    String editCarton(@PathVariable(name = "id") Long id, Model model) {
        CartonDTO existingCarton = cartonClient.getCartonById(id);
        model.addAttribute(CARTONTXT, existingCarton);
        return "Carton/edit";
    }

    @PostMapping("/submit")
    public ModelAndView submitCarton(@ModelAttribute("carton") CartonDTO cartonDTO) {
        cartonClient.createCarton(cartonDTO);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/edit/{id}")
    ModelAndView editCarton(@PathVariable(name = "id") Long id, CartonDTO cartonDTO) {
        cartonClient.updateCarton(id, cartonDTO);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteCartonById(@PathVariable("id") Long id, Model model) {
        cartonClient.deleteCartonById(id);
        return new ModelAndView(REDIRECTTXT);
    }

}
