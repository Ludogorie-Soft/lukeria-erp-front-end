package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.CartItemDTO;
import com.example.LukeriaFrontendApplication.dtos.ShoppingCartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "lukeria-erp-shopping-cart", url = "${backend.base-url}/shoppingCart")
public interface ShoppingCartClient {

    @PostMapping("/addToCart")
    void addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity, @RequestHeader("Authorization") String auth);

    @GetMapping("/showCart")
    List<CartItemDTO> showCart(@RequestHeader("Authorization") String auth);

    @PostMapping("/removeCartItem")
    void removeCartItem(@RequestParam("cartItemId") Long cartItemId, @RequestHeader("Authorization") String auth);

    @PutMapping("/updateQuantity")
    void updateQuantity(@RequestParam("cartItemId") Long cartItemId, int quantity, @RequestHeader("Authorization") String auth);

}
