package com.example.LukeriaFrontendApplication.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
public class MenuController {
    @GetMapping("/index")
    public String index(){
        return "index";
    }
    @GetMapping("/register")
    public String register(){
        return "/register";
    }
    @GetMapping("/blank")
    public String blank(){
        return "pages-blank";
    }

}
