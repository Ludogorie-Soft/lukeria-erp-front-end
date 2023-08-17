package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {

    private final ImageService backendService;

    public UploadController(ImageService backendService) {
        this.backendService = backendService;
    }

    @GetMapping("/upload")
    public String upload(){
        return "/test";
    }
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            backendService.uploadImage(file);
            return "redirect:/success";
        } else {
            return "redirect:/error";
        }
    }
}

