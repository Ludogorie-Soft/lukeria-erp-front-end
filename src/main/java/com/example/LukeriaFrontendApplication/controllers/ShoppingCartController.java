package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ImageClient;
import com.example.LukeriaFrontendApplication.config.PackageClient;
import com.example.LukeriaFrontendApplication.config.ProductClient;
import com.example.LukeriaFrontendApplication.config.ShoppingCartClient;
import com.example.LukeriaFrontendApplication.dtos.CartItemDTO;
import com.example.LukeriaFrontendApplication.dtos.CartItemHelper;
import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import com.example.LukeriaFrontendApplication.dtos.ProductDTO;
import com.example.LukeriaFrontendApplication.dtos.ShoppingCartDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    private final ShoppingCartClient shoppingCartClient;
    private final ProductClient productClient;
    private final PackageClient packageClient;
    private final ImageClient imageService;
    @Value("${backend.base-url}/images")
    private String backendBaseUrl;

    private static final String SESSION_TOKEN = "sessionToken";

    @PostMapping("/addToCart")
    public ModelAndView addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        ModelAndView modelAndView = new ModelAndView("redirect:/product/for-sale");
        try {
            shoppingCartClient.addToCart(productId, quantity, token);
        } catch (IllegalArgumentException ex) {
            modelAndView.addObject("error", "Няма толкова налично количество!");
            return modelAndView;
        }
        modelAndView.addObject("message", "Успешно добавихте към количката!");
        return modelAndView;
    }

    @GetMapping("/showCart")
    public String showMyCart(Model model, HttpServletRequest request){
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<CartItemDTO> cartItems = shoppingCartClient.showCart(token);
        List<CartItemHelper> cartItemsForShowing = new ArrayList<>();
        BigDecimal totalPrice =BigDecimal.valueOf(0);

        for(CartItemDTO cartItem : cartItems ){
            ProductDTO product = productClient.getProductById(cartItem.getProductId(),token);
            BigDecimal price = BigDecimal.valueOf(cartItem.getPrice()*cartItem.getQuantity());
            totalPrice = totalPrice.add(price);;
            cartItemsForShowing.add(new CartItemHelper(cartItem.getId(),product,cartItem.getQuantity(),price));
        }
        List<PackageDTO> packages = packageClient.getAllPackages(token);

        Map<Long, String> productPackageMap = new HashMap<>();
        for (PackageDTO packageDTO : packages) {
            productPackageMap.put(packageDTO.getId(), packageDTO.getName());
        }
        Map<Long, String> productPackageMapImages = new HashMap<>();
        for (PackageDTO packageDTO : packages) {
            if (packageDTO.getPhoto() != null) {
                productPackageMapImages.put(packageDTO.getId(), packageDTO.getPhoto());
            }
        }
        for (PackageDTO packageDTO : packages) {
            if (packageDTO.getPhoto() != null) {
                imageService.getImage(packageDTO.getPhoto());
            }
        }
        model.addAttribute("items", cartItemsForShowing);
        model.addAttribute("productPackageMapImages", productPackageMapImages);
        model.addAttribute("backendBaseUrl", backendBaseUrl);
        model.addAttribute("packages", packages);
        model.addAttribute("productPackageMap", productPackageMap);
        model.addAttribute("totalPrice", totalPrice);

        return "Cart/show";
    }
    @PostMapping("/removeItem")
    public ModelAndView removeItem(@RequestParam(name = "cartItemId")Long cartItemId,HttpServletRequest request ){
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        shoppingCartClient.removeCartItem(cartItemId,token);
        return new ModelAndView("redirect:/shoppingCart/showCart");
    }
    @PostMapping("/updateQuantity")
    public ModelAndView updateQuantity(@RequestParam(name = "cartItemId")Long cartItemId, @RequestParam(name = "quantity")int quantity, HttpServletRequest request ){
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        shoppingCartClient.updateQuantity(cartItemId,quantity,token);
        return new ModelAndView("redirect:/shoppingCart/showCart");
    }
}
