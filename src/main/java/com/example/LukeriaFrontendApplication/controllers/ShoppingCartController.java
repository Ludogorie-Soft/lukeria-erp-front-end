package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ShoppingCartClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    private final ShoppingCartClient shoppingCartClient;
    private static final String SESSION_TOKEN = "sessionToken";

    @PostMapping("/addToCart")
    public ModelAndView addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        ModelAndView modelAndView = new ModelAndView("redirect:/product/for-sale");
        try {
            shoppingCartClient.addToCart(productId, quantity, token);
        } catch (IllegalArgumentException ex) {
            modelAndView.addObject("error","Няма толкова налично количество!");
            return modelAndView;
        }
        modelAndView.addObject("message","Успешно добавихте към количката!");
        return modelAndView;
    }

}
