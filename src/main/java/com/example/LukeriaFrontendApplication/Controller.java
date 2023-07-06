package com.example.LukeriaFrontendApplication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
public class Controller {
    @GetMapping("/index")
    public String index(){
        return "index";
    }
    @GetMapping("/login")
    public String signin(){
        return "login";
    }
    @GetMapping("/register")
    public String register(){
        return "register";
    }
    @GetMapping("/blank")
    public String blank(){
        return "pages-blank";
    }


}
