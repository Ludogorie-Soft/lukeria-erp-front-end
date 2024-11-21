package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.ShoppingCartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "lukeria-erp-shopping-cart", url = "${backend.base-url}/shoppingCart")
public interface ShoppingCartClient {

    @PostMapping("/addToCart")
    void addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity, @RequestHeader("Authorization") String auth);

    @GetMapping("/showCart")
    ShoppingCartDTO showCart(@RequestHeader("Authorization") String auth);

    @PostMapping("/removeCartItem")
    void removeCartItem(@RequestParam("cartItemId") Long cartItemId);


}
