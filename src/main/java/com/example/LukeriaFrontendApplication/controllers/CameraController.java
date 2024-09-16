package com.example.LukeriaFrontendApplication.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CameraController {

    @GetMapping("/camera")
    public String showCameraPage() {
        return "camera"; // Това е името на Thymeleaf шаблона (camera.html)
    }
    @PostMapping("/submit-string")
    public String handleStringInput(@RequestParam("inputText") String inputText, Model model) {
        // Обработваме получения текст и го показваме на следващата страница
        model.addAttribute("message", "You entered: " + inputText);
        return "result"; // Пренасочваме към друга Thymeleaf страница (result.html)
    }
}
