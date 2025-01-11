package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.AuthenticationClient;
import com.example.LukeriaFrontendApplication.dtos.AuthenticationRequest;
import com.example.LukeriaFrontendApplication.dtos.AuthenticationResponse;
import com.example.LukeriaFrontendApplication.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationClient authenticationClient;
    private final SessionManager sessionManager;
    private static final String REDIRECTTXT = "redirect:/index";

    @GetMapping("/login")
    public String login(Model model, AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest) {
        String message = httpServletRequest.getParameter("message");
        if (message != null && message.equals("loginRequired")) {
            model.addAttribute("message", "Моля, впишете се, за да достъпите защитеното съдържание.");
        }

        String error = (String) httpServletRequest.getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
        }

        return "login";
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        authenticationClient.logout(token);
        sessionManager.invalidateSession(request);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/login")
    public ModelAndView login(AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest) {
        ModelAndView modelAndView = new ModelAndView("login");

        try {
            ResponseEntity<AuthenticationResponse> authenticationResponse = authenticationClient.authenticate(authenticationRequest);

            sessionManager.setSessionToken(httpServletRequest, authenticationResponse.getBody().getAccessToken(), authenticationResponse.getBody().getUser().getRole().toString());

            String redirectUrl = (String) httpServletRequest.getSession().getAttribute("redirectAfterLogin");
            if (redirectUrl != null) {
                httpServletRequest.getSession().removeAttribute("redirectAfterLogin");
                return new ModelAndView("redirect:" + redirectUrl);
            } else {
                return new ModelAndView(REDIRECTTXT);
            }

        } catch (Exception e) {
            modelAndView.addObject("error", "Невалидно име или парола");
            return modelAndView;
        }
    }
}
