package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.UploadFileClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
public class UploadFileController {
    private final UploadFileClient uploadFileClient;

    @GetMapping("/upload/file")
    String createPackage(Model model, HttpServletRequest request) {
        return "uploadFile";
    }

    @PostMapping("/upload/file/submit")
    public ModelAndView uploadFromFile(@RequestParam MultipartFile file) throws IOException {
        uploadFileClient.uploadFromFile(file);
        return new ModelAndView("redirect:/package/show");
    }
}
