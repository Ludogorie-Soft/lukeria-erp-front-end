package com.example.LukeriaFrontendApplication.controllers;

import ch.qos.logback.core.model.Model;
import com.example.LukeriaFrontendApplication.config.AuthenticationClient;
import com.example.LukeriaFrontendApplication.dtos.AuthenticationRequest;
import com.example.LukeriaFrontendApplication.dtos.AuthenticationResponse;
import com.example.LukeriaFrontendApplication.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationClient authenticationClient;
    private final SessionManager sessionManager;
    @GetMapping("login")
    public String login(Model model, AuthenticationRequest authenticationRequest){
        return "login";
    }
    @PostMapping("/login")
    public ModelAndView login(AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest){
        ResponseEntity<AuthenticationResponse> authenticationResponse = authenticationClient.authenticate(authenticationRequest);
        sessionManager.setSessionToken(httpServletRequest, authenticationResponse.getBody().getAccessToken());
        return new ModelAndView("/index");
    }
}
